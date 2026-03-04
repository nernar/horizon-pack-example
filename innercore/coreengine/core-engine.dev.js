var ModAPI = {
	modAPIs: {},
	registerAPI: function(name, api, descr) {
		if (!descr) {
			descr = {};
		}
		if (!descr.name) {
			descr.name = name;
		}
		if (!descr.props) {
			descr.props = {};
		}
		this.modAPIs[name] = {
			api: api,
			descr: descr
		};
		Callback.invokeCallback("API:" + name, api, descr);
	},
	requireAPI: function(name) {
		if (this.modAPIs[name]) {
			return this.modAPIs[name].api || null;
		}
		return null;
	},
	requireGlobal: function(name) {
		try {
			return eval(name);
		} catch (e) {
			Logger.Log("ModAPI.requireGlobal for " + name + " failed: " + e, "ERROR");
			return null;
		}
	},
	requireAPIdoc: function(name) {
		if (this.modAPIs[name]) {
			return this.modAPIs[name].descr || null;
		}
		return null;
	},
	requireAPIPropertyDoc: function(name, prop) {
		var descr = this.requireAPIdoc(name);
		if (descr) {
			return descr.props[prop] || null;
		}
		return null;
	},
	getModByName: function(modName) {
		logDeprecation("ModAPI.getModByName()");
		return null;
	},
	isModLoaded: function(modName) {
		logDeprecation("ModAPI.isModLoaded()");
		return false;
	},
	addAPICallback: function(apiName, func) {
		if (this.modAPIs[apiName]) {
			func(this.requireAPI(apiName));
		} else {
			Callback.addCallback("API:" + apiName, func);
		}
	},
	addModCallback: function(modName, func) {
		logDeprecation("ModAPI.addModCallback()");
		if (this.isModLoaded(modName)) {
			func(this.getModByName(modName));
		} else {
			Callback.addCallback("ModLoaded:" + modName, func);
		}
	},
	getModList: function() {
		logDeprecation("ModAPI.getModList()");
		return [];
	},
	getModPEList: function() {
		logDeprecation("ModAPI.getModPEList()");
		return [];
	},
	addTexturePack: function(path) {
		logDeprecation("ModAPI.addTexturePack()");
	},
	cloneAPI: function(api, deep) {
		var cloned = {};
		for (var name in api) {
			var prop = api[name];
			if (deep && prop && (prop.push || prop + "" == "[object Object]")) {
				cloned[name] = this.cloneAPI(prop, false);
			} else {
				cloned[name] = prop;
			}
		}
		return cloned;
	},
	inheritPrototypes: function(source, target) {
		for (var name in source) {
			if (!target[name]) {
				target[name] = source[name];
			}
		}
		return target;
	},
	cloneObject: function(source, deep, rec) {
		if (!rec) {
			rec = 0;
		}
		if (rec > 6) {
			Logger.Log("object clone failed: stackoverflow at " + source, "WARNING");
			return source;
		}
		if (source + "" == "undefined") {
			return undefined;
		}
		if (source == null) {
			return null;
		}
		var cloned = {};
		for (var name in source) {
			var prop = source[name];
			if (deep && typeof(prop) == "object") {
				cloned[name] = this.cloneObject(prop, true, rec + 1);
			} else {
				cloned[name] = prop;
			}
		}
		return cloned;
	},
	debugCloneObject: function(source, deep, rec) {
		if (!rec) {
			rec = 0;
		}
		if (rec > 5) {
			return "stackoverflow";
		}
		if (source + "" == "undefined") {
			return undefined;
		}
		if (source == null) {
			return null;
		}
		var cloned = {};
		for (var name in source) {
			var prop = source[name];
			if (deep && typeof(prop) == "object") {
				cloned[name] = this.cloneObject(prop, true, rec + 1);
			} else {
				cloned[name] = prop;
			}
		}
		return cloned;
	}
};

var GameAPI = {
	message: function(msg) {
		clientMessage(msg + "");
	},
	tipMessage: function(msg) {
		tipMessage(msg + "");
	},
	dialogMessage: function(message, title) {
		GuiUtils.Run(function() {
			var ctx = getMcContext();
			var builder = android.app.AlertDialog.Builder(ctx);
			if (title) {
				builder.setTitle(title + "");
			}
			if (message) {
				message += "";
				message = message.split("\n")
					.join("<br>");
				builder.setMessage(android.text.Html.fromHtml(message));
			}
			builder.show();
		});
	}
};

var PlayerAPI = {
	get: function() {
		return getPlayerEnt();
	},
	getDimension: function() {
		return Player.getDimension();
	},
	getPosition: function() {
		var pos = Entity.getPosition(getPlayerEnt());
		return {
			x: pos[0],
			y: pos[1],
			z: pos[2]
		};
	}
};

var __RAD_TO_DEGREES = 180 / Math.PI;

function __radToDegrees(x) {
	return x * __RAD_TO_DEGREES;
}

function __degreesToRad(x) {
	return x / __RAD_TO_DEGREES;
}

var EntityAPI = {
	getType: function(ent) {
		return Entity.getEntityTypeId(ent);
	},
	getDimension: function(entity) {
		return Entity.getDimension(entity);
	},
	getPosition: function(ent) {
		var pos = Entity.getPosition(ent);
		return {
			x: pos[0],
			y: pos[1],
			z: pos[2]
		};
	},
	getLookAngle: function(ent) {
		return {
			pitch: __degreesToRad(-Entity.getPitch(ent)),
			yaw: __degreesToRad(Entity.getYaw(ent))
		};
	}
};

// var NativeAPI_getTileAndData = requireMethodFromNativeAPI("api.NativeAPI", "getTileAndData");
var NativeAPI_getTile = requireMethodFromNativeAPI("api.NativeAPI", "getTile");
var NativeAPI_getData = requireMethodFromNativeAPI("api.NativeAPI", "getData");

var WorldAPI = {
	nativeGetBlockID: function(x, y, z) {
		return NativeAPI_getTile(x, y, z);
	},
	nativeGetBlockData: function(x, y, z) {
		return NativeAPI_getData(x, y, z);
	},
	// getBlock: function(x, y, z) {
		// var tile = NativeAPI_getTileAndData(x, y, z);
		// return {
			// id: ((tile >> 24 == 1) ? -1 : 1) * (tile & 0xFFFF),
			// data: ((tile >> 16) & 0xFF)
		// };
	// },
	getBlockID: NativeAPI_getTile,
	getBlockData: NativeAPI_getData,
	isChunkLoaded: function(x, z) {
		return Level.isChunkLoaded(x, z);
	},
	isChunkLoadedAt: function(x, y, z) {
		return Level.isChunkLoadedAt(x, y, z);
	},
	getBiome: function(x, z) {
		return Level.getBiome(x, z);
	},
	// getGrassColor: function(x, z) {
		// return Level.getGrassColor(x, z);
	// },
	// getGrassColorRGB: function(x, z) {
		// var color = Level.getGrassColor(x, z);
		// return {
			// r: (color >> 16) & 255,
			// g: (color >> 8) & 255,
			// b: (color >> 0) & 255
		// };
	// },
	canSeeSky: function(x, y, z) {
		return GenerationUtils.canSeeSky(x, y, z);
	}
};

var CoreAPI = {
	Logger: Logger,
	Translation: Translation,
	Config: Config,
	Callback: Callback,
	ModAPI: ModAPI,
	World: WorldAPI,
	Entity: EntityAPI,
	Player: PlayerAPI,
	Game: GameAPI
};

function injectCoreAPI(scope) {
	for (var name in CoreAPI) {
		scope[name] = CoreAPI[name];
	}
}