

target_path = "/Users/simonvermeir/Documents/industrial-engineering/SchoolCurrent/MasterProef/Master-Thesis-SSP/data/filenames"

base = 'DAT_D'
names = []

print("{")
for i in range(1,2):
    for j in range(0,10):
        print("\"", end="")
        n = base + str(i) +  '_' + str(j)
        print(n, end="")
        print("\"", end="")
        print(",")

print("}", end="")


# with open(target_path + name + ".txt", "w") as write_file:
#     for name in names:
#         write_file.write(name)
