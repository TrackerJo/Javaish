from naoqi import ALProxy
tts = ALProxy("ALTextToSpeech", "null", 9559)
tts.say(""Hello World, This is running from Javaish"")
motion = ALProxy("ALMotion", "null", 9559)
motion.setStiffnesses("Body", 1.0)
motion.moveInit()
motion.moveTo(2,0,0)
postureService = ALProxy("ALRobotPosture", "null", 9559)
result = postureService.goToPosture("Stand", .8)
postureService.goToPosture("Sit", .8)
print("tst")
def local():
	name = "Nathaniel"
	print(name)
print("Hello")
