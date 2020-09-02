


value = 6.83
ran = (3,14)
count = 30
values = [0] * 30


def deeper(index):
    for a in range(ran[0],ran[1]):
        values[index] = a
        if (index is len(values) - 1):
            r = average(values)
            #print(r)
            #print(values)

            if r == value:
                print("*****SOLUTION****")
                print(values)
                print(r)
                print("*********")
        else:
            index = index + 1
            deeper(index)


def average(values):
    total = 0
    for i in values:
        total += i

    avg = total/len(values)
    return round(avg, 2)



deeper(0)

