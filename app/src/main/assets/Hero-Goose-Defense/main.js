window.boot = function () {
  var settings = window._CCSettings;
  window._CCSettings = undefined;
  var onProgress = null;
  var RESOURCES = cc.AssetManager.BuiltinBundleName.RESOURCES;
  var INTERNAL = cc.AssetManager.BuiltinBundleName.INTERNAL;
  var MAIN = cc.AssetManager.BuiltinBundleName.MAIN;
  function setLoadingDisplay() {
    // Loading splash scene
    var splash = document.getElementById("splash");
    var progressBar = splash.querySelector(".progress-bar span");
    onProgress = function (finish, total) {
      var percent = (100 * finish) / total;
      if (progressBar) {
        progressBar.style.width = percent.toFixed(2) + "%";
      }
    };
    splash.style.display = "block";
    progressBar.style.width = "0%";

    cc.director.once(cc.Director.EVENT_AFTER_SCENE_LAUNCH, function () {
      splash.style.display = "none";
    });
  }

  var onStart = function () {
    cc.view.resizeWithBrowserSize(true);

    // if (cc.sys.isBrowser) {
    // setLoadingDisplay();
    // }

    if (cc.sys.isMobile) {
      cc.view.enableRetina(true);
    } else {
      cc.view.enableRetina(false);
    }

    if (cc.sys.isBrowser && cc.sys.os === cc.sys.OS_ANDROID) {
      cc.assetManager.downloader.maxConcurrency = 2;
      cc.assetManager.downloader.maxRequestsPerFrame = 2;
    }

    var launchScene = settings.launchScene;
    var bundle = cc.assetManager.bundles.find(function (b) {
      return b.getSceneInfo(launchScene);
    });

    bundle.loadScene(launchScene, null, onProgress, function (err, scene) {
      console.error("load scene: " + scene.name);
      if (!err) {
        cc.director.runSceneImmediate(scene);
        // if (cc.sys.isBrowser) {
        //     // show canvas
        //     var canvas = document.getElementById('GameCanvas');
        //     canvas.style.visibility = '';
        //     var div = document.getElementById('GameDiv');
        //     if (div) {
        //         div.style.backgroundImage = '';
        //     }
        //     console.log('Success to load scene: ' + launchScene);
        // }

        var sceneName = cc.director.getScene();
        if (sceneName.name === "Loading") {
          var sceneNode = cc.director.getScene().children[0];

          //TODO Add Image Overlay
          var overlay = new cc.Node("Overlay");
          var sprite = overlay.addComponent(cc.Sprite);

          var img = new Image();
          img.src = "resources/overlay.png"; // Đường dẫn ảnh

          img.onload = function () {
            // Tạo texture từ ảnh đã load
            var texture = new cc.Texture2D();
            texture.initWithElement(img);
            texture.handleLoadedTexture();

            // Tạo sprite frame từ texture
            var spriteFrame = new cc.SpriteFrame(texture);
            sprite.spriteFrame = spriteFrame;

            // Kích thước màn hình
            var winSize = cc.winSize;
            overlay.setAnchorPoint(0.5, 0.5); // Đặt anchorPoint của overlay ở giữa
            // overlay.setPosition(winSize.width / 2, winSize.height / 2);
            overlay.setPosition(0, 0);
            // overlay.setContentSize(winSize);
            overlay.zIndex = -9999;

            // Scale để ảnh che hết màn hình
            overlay.scaleX = winSize.width / img.width;
            overlay.scaleY = winSize.height / img.height;

            sceneNode.addChild(overlay);
          };

          cc.director.getScene().children[0].children[4].active = false; // con ngỗng
          cc.director.getScene().children[0].children[2].active = false;
          cc.director.getScene().children[0].children[2].children[0].active = false; // progress bar trên
          cc.director.getScene().children[0].children[3].active = false;
          cc.director.getScene().children[0].children[3].children[0].active = false; // progress bar dưới
          cc.director.getScene().children[0].children[6].active = false;
          for (var i = 0; i < sceneNode.children.length; i++) {
            var child = sceneNode.children[i];
            if (i == 1) {
              child.active = false; // hide loading scene
            }
            console.error(
              "Child " + i + ": " + child.name + " is active: " + child.active
            );
          }
        }
      }
    });
  };

  var option = {
    id: "GameCanvas",
    debugMode: settings.debug
      ? cc.debug.DebugMode.INFO
      : cc.debug.DebugMode.ERROR,
    showFPS: settings.debug,
    frameRate: 60,
    groupList: settings.groupList,
    collisionMatrix: settings.collisionMatrix,
  };

  cc.assetManager.init({
    bundleVers: settings.bundleVers,
    remoteBundles: settings.remoteBundles,
    server: settings.server,
  });

  var bundleRoot = [INTERNAL];
  settings.hasResourcesBundle && bundleRoot.push(RESOURCES);

  var count = 0;
  function cb(err) {
    // console.error("errorrrr: " + err);
    if (err) return console.error(err.message, err.stack);
    count++;
    if (count === bundleRoot.length + 1) {
      cc.assetManager.loadBundle(MAIN, function (err) {
        if (!err) cc.game.run(option, onStart);
      });
    }
  }

  cc.assetManager.loadScript(
    settings.jsList.map(function (x) {
      return "src/" + x;
    }),
    cb
  );

  for (var i = 0; i < bundleRoot.length; i++) {
    cc.assetManager.loadBundle(bundleRoot[i], cb);
  }
};

if (window.jsb) {
  var isRuntime = typeof loadRuntime === "function";
  if (isRuntime) {
    require("src/settings.js");
    require("src/cocos2d-runtime.js");
    if (CC_PHYSICS_BUILTIN || CC_PHYSICS_CANNON) {
      require("src/physics.js");
    }
    require("jsb-adapter/engine/index.js");
  } else {
    require("src/settings.js");
    require("src/cocos2d-jsb.js");
    if (CC_PHYSICS_BUILTIN || CC_PHYSICS_CANNON) {
      require("src/physics.js");
    }
    require("jsb-adapter/jsb-engine.js");
  }

  cc.macro.CLEANUP_IMAGE_CACHE = true;
  window.boot();
}
