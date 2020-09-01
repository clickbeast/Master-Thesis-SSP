
lines = []
with open('test.txt') as f:
    for line in f:
        if line.startswith("["):
            l = line.replace("[", "").replace("]","").strip().replace(" ", "").split(",")
            l = [int(a) for a in l]
            lines.append(l)


pears  = []

for a in lines:
    allowed = True
    for b in a:
        if b is 3:
            allowed = False

    if allowed:
        pears.append(a)


print(lines)
print(pears)