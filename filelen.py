
if __name__ == '__main__':
    output = open('output.txt', 'r')
    lineLen = len(output.readlines(0))
    print(output.readline(1))
    print('Line length: {0}\nFile length: {1} lines'.format(
        len(output.readlines()),
        lineLen))
