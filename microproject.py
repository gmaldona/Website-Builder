import os
import re
import sys

def wc_dir(dir: str) -> dict:
    directory = "cd {};".format(dir)
    files = {}
    
    stream = os.popen("{}ls".format(directory))
    file_names = stream.readlines()
    file_names = [name.strip() for name in file_names]
    for file_name in file_names:
        if os.path.isfile(file_name):
            files[file_name] = 0

    for file_name in file_names: 
        try: 
            stream = os.popen("{}wc -l {}".format(directory, file_name))
            output = stream.readlines()
            files[file_name] = re.findall('[0-9]+', output[0])[0]
        except:
            continue
    
    return files

if __name__ == '__main__':
    files = wc_dir(sys.argv[1])
    
    for file_name in files.keys():
        print("{} : {}".format(file_name, files[file_name]))
    
    