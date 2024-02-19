names = ["Nathaniel", "Dever", "Sarah", "Sydney", "Fred", "Kathleen"]
ages = [17, 21, 13, 25, 57, 57]
x = len(names)
while x > 0:
	print(names[x - 1] + " is " + str(ages[x - 1]) + " years old")
	x -= 1
	print(x)
