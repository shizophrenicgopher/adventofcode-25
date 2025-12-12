input = open("input12.txt").read().split("\n\n")
shapes = list(map(lambda x: x.split("\n")[1:], input[0:-1]))
targets = list(map(lambda line: ((int((x := line.split(": ")[0].split("x"))[0]), int(x[1])),
                                 list(map(int, line.split(": ")[1].split()))), input[-1].split("\n")))

part1 = 0
for target in targets:
    requiredSize = 0
    for i in range(len(shapes)):
        requiredSize += sum(e.count('#') for e in shapes[i]) * target[1][i]
    part1 += requiredSize > target[0][0] * target[0][1]

print("Part 1:", 1000 - part1)
