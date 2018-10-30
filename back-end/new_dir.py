#!/usr/bin python
'''
Create a new directory for some project. 
Prints the ID of the newly created directory.
'''
__filename__ = "new_dir.py"
__author__ = "Enrique Rodriguez"
__email__ = "enrique.rodriguez9@upr.edu"
__version__ = "1.0.0"
__credits__ = ["Bryan Pesquera", "David Ortiz", "Enrique Rodriguez", "Kevin Legarreta", "Luis Mieses"]
__copyright__ = "Copyright 2018, Grafia"


from uuid import uuid4                      # Used for naming project directories with random identifiers.
from os import mkdir                        # Used for creating the directory.
import json                                 # Used for outputing the directory name.

PATH = "/var/www/html/projects/"            # Location where the directories will be stored.

def main():
    directory_name = str(uuid4())
    no_file_created = True

    while no_file_created:
        try:
            mkdir(PATH + directory_name)
            no_file_created = False
        except FileExistsError:
            directory_name = str(uuid4())   # Use a different name if it already exists (Unlikely, but just in case).
    
    directory = {"folder_link" : directory_name}
    print json.dumps(directory)             # i.e. {"folder_link" : "8dafab93-1342-404b-87c1-104f26e9a14b"}

if __name__ == "__main__" : main()
