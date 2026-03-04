#include <memory>

#include <callbacks.h>
#include <jni.h>
#include <hook.h>
#include <logger.h>
#include <mod.h>
#include <symbol.h>

#include "includes/innercore/vtable.h"

/* Reverse-engineered Minecraft PE internal structures mapping. 
 * Memory layouts, vtable offsets, and padding arrays (filler) must strictly match the target libminecraftpe.so architecture. 
 * Any misalignment here will result in segmentation faults during runtime.
 */

// Entity::save
#define ENTITY_VTABLE_OFFSET_GET_ENTITY_TYPE_ID 64

typedef struct {
	void displayClientMessage(std::string const&);
	void showTipMessage(std::string const&);
	int getNumSlots();
} Gui;

typedef struct {
	Gui* getGui();
} MinecraftClient;

/* Represents the base memory layout of the native Entity class.
 * VTable offsets are highly version-dependent and must be recalculated if the base game version changes.
 */
typedef struct Entity {
	void** vtable;
	int filler3[7];
	float x;
	float y;
	float z;
	char filler[8];
	int entityId; // Arrow::Arrow(Mob*)
	char filler2[28];
	float motionX; // Entity::rideTick
	float motionY;
	float motionZ;
	float yaw;
	float pitch;
	float prevYaw;
	float prevPitch;
	char filler4[132];
	int renderType;
	struct Entity* rider;
	struct Entity* riding;
};
typedef Entity Player;

struct FullTile {
	unsigned char id;
	unsigned char data;
	FullTile(): id(0), data(0) {}
	FullTile(unsigned char newId, unsigned char newData): id(newId), data(newData) {}
	FullTile(FullTile const& other): id(other.id), data(other.data) {}
};

struct TilePos {
	int x;
	int y;
	int z;
	TilePos(TilePos const& other) : x(other.x), y(other.y), z(other.z) {}
	TilePos(int dx, int dy, int dz): x(dx), y(dy), z(dz) {}
	TilePos(): x(0), y(0), z(0) {}
};

struct ChunkPos {
	int x;
	int z;
	ChunkPos(ChunkPos const& other) : x(other.x), z(other.z) {}
	ChunkPos(int cx, int cz): x(cx), z(cz) {}
	ChunkPos(): x(0), z(0) {}
};

typedef void LevelChunk;

typedef struct {
	void** vtable;
	char filler[40];
	std::string name; // Biome::setName
	char filler2[120-48];
	int id; // Biome::Biome
} Biome;

typedef struct {
	FullTile getTile(int, int, int);
	unsigned char getData(int, int, int);
	bool hasChunksAt(int, int, int, int);
	bool canSeeSky(int, int, int);
	Biome* getBiome(TilePos const&);
	LevelChunk* getChunk(int, int);
} TileSource;

typedef struct {
	void** vtable;
	char filler[4];
	bool isRemote;
	char filler2[2967];
	TileSource* tileSource; // Level::getChunkSource

	Entity* getEntity(int, bool);
} Level;

typedef struct {
	bool isInGameScreen();
} Screen;

typedef void ScreenChooser;

MinecraftClient* client;
Player* localPlayer;
Level* level;

/* Core hooking module for intercepting native game state changes.
 * Resolves mangled C++ symbols from libminecraftpe.so and binds native lifecycle events 
 * (level loading, entity spawning, ticking) to Java-side listeners.
 */
class InstantReferrerModule : public Module {
public:
	InstantReferrerModule(): Module("instant") {}

	virtual void initialize() {
		DLHandleManager::initializeHandle("libminecraftpe.so", "mcpe");

		/* Native hook definitions. Uses symbol mangling (Itanium ABI) to target specific functions.
		 * Captured pointers (client, level, localPlayer) are stored globally for later JNI API access.
		 * Triggers asynchronous Java events via JavaCallbacks::invokeCallback.
		 */
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN8GameMode10initPlayerEP6Player"),
			LAMBDA((void* mode, Player* player), {
				localPlayer = player;
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN15MinecraftClient8setLevelERSt10unique_ptrI5LevelSt14default_deleteIS1_EERKSsP6Player"),
			LAMBDA((MinecraftClient* __client, std::unique_ptr<Level>& __level, std::string const& progressLabel, Player* player), {
				Logger::debug("InstantReferrer", "MinecraftClient::setLevel(progressLabel=%s)", progressLabel.c_str());
				client = __client;
				level = __level.get();
				localPlayer = player; // will be changed, may be omitted
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN15MinecraftClient9setScreenEP6Screen"),
			LAMBDA((MinecraftClient* client, Screen* screen), {
				Logger::debug("InstantReferrer", "MinecraftClient::setScreen(inGame=%d)", screen->isInGameScreen());
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);

		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN5Level9addPlayerEP6Player"),
			LAMBDA((Level* level, Player* player), {
				JavaCallbacks::invokeCallback("onEntityAdded", "(J)V", player->entityId);
				// Logger::debug("InstantReferrer", "Level::addPlayer(player=%d)", player->entityId);
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN5Level9addEntityEP6Entity"),
			LAMBDA((Level* level, Entity* entity), {
				JavaCallbacks::invokeCallback("onEntityAdded", "(J)V", entity->entityId);
				// Logger::debug("InstantReferrer", "Level::addEntity(entity=%d)", entity->entityId);
			}, ),
			HookManager::RETURN | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN5Level12removeEntityER6Entity"),
			LAMBDA((Level* level, Entity& entity), {
				JavaCallbacks::invokeCallback("onEntityRemoved", "(J)V", entity.entityId);
				// Logger::debug("InstantReferrer", "Level::removeEntity(entity=%d)", entity.entityId);
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN13ScreenChooser9setScreenE8ScreenId"),
			LAMBDA((ScreenChooser* level, int screenId), {
				Logger::debug("InstantReferrer", "ScreenChooser::setScreen(screen=%d)", screenId);
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);

		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN9Minecraft14onPlayerLoadedER6Player"),
			LAMBDA((void* minecraft, Player& player), {
				Logger::debug("InstantReferrer", "Minecraft::onPlayerLoaded(player=%d)", player.entityId);
				JavaCallbacks::invokeCallback("onLevelCreated", "()V");
				// TODO: JavaCallbacks::invokeCallback("onLocalServerStarted", "()V");
				JavaCallbacks::invokeCallback("onLevelDisplayed", "()V");
			}, ),
			HookManager::RETURN | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN9Minecraft9leaveGameEbb"),
			LAMBDA((void* minecraft, bool bool1, bool bool2), {
				Logger::debug("InstantReferrer", "Minecraft::leaveGame(%d, %d)", bool1, bool2);
				JavaCallbacks::invokeCallback("onGameStopped", "(Z)V", true);
			}, ),
			HookManager::CALL | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN12GameRenderer4tickEii"),
			LAMBDA((MinecraftClient* client, int int1, int int2), {
				// Logger::debug("InstantReferrer", "GameRenderer::tick(%d, %d)", int1, int2);
				// TODO: JavaCallbacks::invokeCallback("onLocalTick", "()V");
			}, ),
			HookManager::RETURN | HookManager::LISTENER
		);
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN5Level4tickEv"),
			LAMBDA((Level* level), {
				// Logger::debug("InstantReferrer", "Level::tick()");
				// TODO: JavaCallbacks::invokeCallback("onTick", "()V");
			}, ),
			HookManager::RETURN | HookManager::LISTENER
		);

		/* Intercepts the game version string query to inject a custom identifier ("/blic").
		 * Operates as a HookManager::CONTROLLER to modify the return value directly.
		 */
		HookManager::addCallback(
			SYMBOL("mcpe", "_ZN6Common20getGameVersionStringEv"),
			LAMBDA((HookManager::CallbackController* controller), {
				std::string* result = (std::string*) controller->getResult();
				size_t offset = result->find(" ");
				if (offset == std::string::npos) {
					result->append("/blic");
				} else {
					result->insert(offset, "/blic");
				}
			}, ),
			HookManager::RETURN | HookManager::LISTENER | HookManager::CONTROLLER
		);

		// HookManager::addCallback(
			// SYMBOL("mcpe", "_ZN3Gui11getNumSlotsEv"),
			// LAMBDA((Gui* gui), {
				// Logger::debug("InstantReferrer", "Slots: %d", (int) controller->getResult());
				// controller->replace();
				// return 8;
			// }, ),
			// HookManager::REPLACE | HookManager::CALL
		// );
	}
};

/* JNI Entry point triggered upon System.loadLibrary().
 * Attaches the current thread to the JVM and registers the default native callback handler path.
 */
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void*) {
	JNIEnv* env = nullptr;
	ATTACH_JAVA(env, JNI_VERSION_1_4) {
		JavaCallbacks::setDefaultCallbackClass(env, "com/zhekasmirnov/innercore/api/NativeCallback");
	}
	return JNI_VERSION_1_6;
} NO_JNI_MAIN {
	Module* instant = new InstantReferrerModule();
};

/* Helper method to safely resolve a native Entity pointer from an integer ID.
 * Depends on the global `level` pointer being populated by the MinecraftClient::setLevel hook.
 */
Entity* unwrapEntity(int uid) {
	if (level == nullptr) return nullptr;
	return level->getEntity(uid, false);
}

extern "C" {
	/* JNI Bridge API. Exposes native game functions and state accessors to the Java-side modding API.
	 * Method names must perfectly match the fully qualified Java class path: com.zhekasmirnov.innercore.api.NativeAPI
	 */
	JNIEXPORT jstring JNICALL Java_com_zhekasmirnov_innercore_api_NativeCallback_getStringParam
		(JNIEnv *env, jclass clazz, jstring param) {
			return param;
		}

	JNIEXPORT void JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_clientMessage
		(JNIEnv *env, jclass clazz, jstring message) {
			if (client == nullptr) return;
			const char* cmessage = env->GetStringUTFChars(message, NULL);
			std::string target = std::string(cmessage);
			client->getGui()->displayClientMessage(target);
			env->ReleaseStringUTFChars(message, cmessage);
		}

	JNIEXPORT void JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_tipMessage
		(JNIEnv *env, jclass clazz, jstring message) {
			if (client == nullptr) return;
			const char* cmessage = env->GetStringUTFChars(message, NULL);
			std::string target = std::string(cmessage);
			client->getGui()->showTipMessage(target);
			env->ReleaseStringUTFChars(message, cmessage);
		}

	JNIEXPORT jlong JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getPlayer
		(JNIEnv *env, jclass clazz) {
			if (localPlayer == nullptr) return 0;
			return localPlayer->entityId;
		}

	JNIEXPORT jlong JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getLocalPlayer
		(JNIEnv *env, jclass clazz) {
			if (localPlayer == nullptr) return 0;
			return localPlayer->entityId;
		}

	/* Reads raw positional data directly from the entity memory struct to minimize JNI overhead.
	 * Faster than invoking native getter methods.
	 */
	JNIEXPORT void JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getPosition
		(JNIEnv *env, jclass clazz, jlong entityId, jfloatArray positionArray) {
			if (positionArray == nullptr) return;
			Entity* entity = unwrapEntity(static_cast<int>(entityId));
			if (entity == nullptr) return;
			float position[3] = { entity->x, entity->y, entity->z };
			env->SetFloatArrayRegion(positionArray, 0, 3, position);
		}

	JNIEXPORT void JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getRotation
		(JNIEnv *env, jclass clazz, jlong entityId, jfloatArray rotationArray) {
			if (rotationArray == nullptr) return;
			Entity* entity = unwrapEntity(static_cast<int>(entityId));
			if (entity == nullptr) return;
			float rotation[2] = { entity->yaw, entity->pitch };
			env->SetFloatArrayRegion(rotationArray, 0, 2, rotation);
		}

	JNIEXPORT jint JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getEntityDimension
		(JNIEnv *env, jclass clazz, jlong entityId) {
			return 0; // what is dimension? :>
		}

	JNIEXPORT jint JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getEntityType
		(JNIEnv *env, jclass clazz, jlong entityId) {
			Entity* entity = unwrapEntity(static_cast<int>(entityId));
			if (entity == nullptr) return 0;
			/* Dynamically resolves the virtual function getEntityTypeId() via the VTable to avoid direct symbol reliance.
			 * The pointer is cast to a function taking an Entity* (acting as the 'this' pointer) and returning an int.
			 * Bitwise AND 0xff is applied to extract the exact entity byte ID.
			 */
			void* vtable = entity->vtable[ENTITY_VTABLE_OFFSET_GET_ENTITY_TYPE_ID];
			int (*fn)(Entity*) = (int (*) (Entity*)) vtable;
			return fn(entity) & 0xff;
			// TODO: I'm pretty sure it was overriden somewhere...
			// return entity->getEntityTypeId();
		}

	JNIEXPORT jboolean JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_isValidEntity
		(JNIEnv *env, jclass clazz, jlong entityId) {
			Entity* entity = unwrapEntity(static_cast<int>(entityId));
			return entity != nullptr;
		}

	JNIEXPORT jint JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getTile
		(JNIEnv *env, jclass clazz, jint x, jint y, jint z) {
			if (level == nullptr) return 0;
			TileSource* region = level->tileSource;
			if (region == nullptr) return 0;
			FullTile tile = region->getTile(x, y, z);
			return tile.id;
		}

	JNIEXPORT jint JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getData
		(JNIEnv *env, jclass clazz, jint x, jint y, jint z) {
			if (level == nullptr) return 0;
			TileSource* region = level->tileSource;
			if (region == nullptr) return 0;
			return region->getData(x, y, z);
		}

	JNIEXPORT jboolean JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_isChunkLoaded
		(JNIEnv *env, jclass clazz, jint x, jint z) {
			if (level == nullptr) return false;
			TileSource* region = level->tileSource;
			if (region == nullptr) return false;
			LevelChunk* chunk = region->getChunk(x, z);
			return chunk != nullptr;
		}

	JNIEXPORT jint JNICALL Java_com_zhekasmirnov_innercore_api_NativeAPI_getBiome
		(JNIEnv *env, jclass clazz, jint x, jint z) {
			if (level == nullptr) return 0;
			TileSource* region = level->tileSource;
			if (region == nullptr) return 0;
			TilePos location(x, 64, z);
			Biome* biome = region->getBiome(location);
			if (biome == nullptr) return 0;
			return biome->id;
		}

	JNIEXPORT jboolean JNICALL Java_com_zhekasmirnov_innercore_api_NativeGenerationUtils_canSeeSky
		(JNIEnv *env, jclass clazz, jint x, jint y, jint z) {
			if (level == nullptr) return false;
			TileSource* region = level->tileSource;
			if (region == nullptr) return false;
			return region->canSeeSky(x, y, z);
		}

	//
	// STUBS, RESERVED FOR FUTURE REMOVALS
	//
	JNIEXPORT void JNICALL Java_com_zhekasmirnov_apparatus_mcpe_NativeNetworking_runMinecraftNetworkEventLoop
		(JNIEnv *env, jclass clazz, jboolean something) {}
	JNIEXPORT void JNICALL Java_com_zhekasmirnov_apparatus_mcpe_NativeNetworking_runServerNetworkEventLoop
		(JNIEnv *env, jclass clazz, jboolean something) {}
	JNIEXPORT void JNICALL Java_com_zhekasmirnov_apparatus_mcpe_NativeNetworking_runClientNetworkEventLoop
		(JNIEnv *env, jclass clazz, jboolean something) {}
}
