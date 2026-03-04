#include <stdlib.h>
#include <functional>
#include <logger.h>
#include <hook.h>
#include <stdarg.h>
#include <jni.h>
#include <list>


#ifndef CALLBACKS_SYSTEM_CALLBACKS_H
#define CALLBACKS_SYSTEM_CALLBACKS_H 

// 4 * 16 = 64 bytes per callback params
#define _MAX_CALLBACK_PARAMS_SIZE 16 
// macro for creating callback lambdas
#define CALLBACK(VALS, ARGS, ...) ((std::function<void(Callbacks::CallbackParamsPlaceholder)>*) new std::function<void ARGS> (VALS ARGS -> void __VA_ARGS__)) 


namespace Callbacks {
	struct CallbackParamsPlaceholder {
		uint32_t bytes[_MAX_CALLBACK_PARAMS_SIZE];  
	};

	class Callback {
	public:
		std::function<void(CallbackParamsPlaceholder)> func;
		struct Callback* next;
		struct Callback* prev; 

		Callback(std::function<void(CallbackParamsPlaceholder)>&);
	};

	// TODO: fuck linked lists
	class CallbackList {
	public:
		struct Callback* first;
		struct Callback* last;

		void add(std::function<void(CallbackParamsPlaceholder)>&);	
		void invoke(CallbackParamsPlaceholder);
	};

	void addCallback(std::string name, std::function<void(CallbackParamsPlaceholder)>* func);
	void invokeCallback(std::string const& name, ...);
	void invokeAsyncCallback(std::string const& name, ...);  // TODO: make this method to run callbacks in separate thread
};

namespace JavaCallbacks {
	enum CallbackFlag {
		PREVENTABLE = 1,
		RECURSIVE = 2,
		SIGNATURE = 4,
		NO_STACK = 8
	};

	enum PreventionMask {
		TARGET = 1,    // prevent target call 
		CALLBACKS = 2  // prevent future native callbacks for this method
	};

	class JavaThreadContainer {
		JNIEnv* env = nullptr;
		bool attached = false;
	public:
		JavaThreadContainer();
		JavaThreadContainer(JNIEnv* env);
		~JavaThreadContainer();

		JNIEnv* get();
	};

	class CallbackStack {
	public:
		class StackElement {
		public:
			jmethodID method;
			StackElement(jmethodID m);
		};

		std::list<StackElement> stack;

		bool isEmpty();
		bool has(jmethodID name);
		void push(jmethodID name);
		void pop();
	};

	void passStringParameter(std::string const& key, std::string const& value);
	std::string getStringParameter(std::string const& key);

	void setJavaVM(JavaVM* vm);
	JavaVM* getJavaVM();
	void setDefaultCallbackClass(JNIEnv *env, std::string path);
	jclass getDefaultCallbackClass();
	
	void setStatisticsCallback(std::function<void(int, int, int)> const& func);
	bool isStatisticsEnabled();

	// invokeCallbackV(class, method&, name, signature, controller, flags, va_list);
	void invokeCallbackV(jclass callbackClass, std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, va_list);
	void invokeCallbackV(jclass callbackClass, jmethodID& method, std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, va_list);
	void invokeCallbackV(jclass callbackClass, jmethodID& method, const char* name, const char* signature, HookManager::CallbackController* controller, int flags, va_list);
	// invokeControlledCallback(class, method&, name, signature, controller, flags, ...);
	void invokeControlledCallback(jclass callbackClass, std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, ...);
	void invokeControlledCallback(jclass callbackClass, jmethodID& method, std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, ...);
	void invokeControlledCallback(jclass callbackClass, jmethodID& method, const char* name, const char* signature, HookManager::CallbackController* controller, int flags, ...);
	// invokeControlledCallback(method&, name, signature, controller, flags, ...);
	void invokeControlledCallback(std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, ...);
	void invokeControlledCallback(jmethodID& method, std::string const& name, std::string const& signature, HookManager::CallbackController* controller, int flags, ...);
	void invokeControlledCallback(jmethodID& method, const char* name, const char* signature, HookManager::CallbackController* controller, int flags, ...);
	// invokeCallback(class, method&, name, signature, ...);
	void invokeCallback(jclass callbackClass, std::string const& name, std::string const& signature, ...);
	void invokeCallback(jclass callbackClass, jmethodID& method, std::string const& name, std::string const& signature, ...);
	void invokeCallback(jclass callbackClass, jmethodID& method, const char* name, const char* signature, ...);
	// invokeCallback(method&, name, signature, ...);
	void invokeCallback(std::string const& name, std::string const& signature, ...);
	void invokeCallback(jmethodID& method, std::string const& name, std::string const& signature, ...);
	void invokeCallback(jmethodID& method, const char* name, const char* signature, ...);

	void prevent(int mask);
	void prevent();
	bool isPrevented(); // returns if target is prevented

	void addExceptionHandler(std::function<void(JNIEnv*, const char*, jthrowable)> handler);
	void handleJavaException(JNIEnv* env, std::string callback, jthrowable throwable);
};

/* helpful macro to attach java env at any moment
JNIEnv* env;
ATTACH_JAVA(env, JNI_VERSION) {
	do something with env
}
*/
#define ATTACH_JAVA(ENV, VER) for (bool wasEnvAttached = (JavaCallbacks::getJavaVM()->GetEnv((void **) &ENV, VER) == JNI_EDETACHED), _flag = true; _flag; wasEnvAttached && JavaCallbacks::getJavaVM()->DetachCurrentThread(), _flag = false)

#define __JAVA_CALLBACK_CONCAT_MACRO(A, B) A ## B
#define __JAVA_CALLBACK_CACHED_METHOD_ID(line) __JAVA_CALLBACK_CONCAT_MACRO(_cached_method_id, line)
#define INVOKE_JAVA_CALLBACK(method, ...) { static jmethodID __JAVA_CALLBACK_CACHED_METHOD_ID(__LINE__) = nullptr; method(__JAVA_CALLBACK_CACHED_METHOD_ID(__LINE__), ##__VA_ARGS__); }

#endif