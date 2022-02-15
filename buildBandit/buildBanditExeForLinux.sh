#!/bin/bash
set -euo pipefail


echo "INFO: checking python installation..."

if command -v python3 &> /dev/null; then
	echo "INFO: found python3."
	
	if command -v pip3 &> /dev/null; then
		echo "INFO: found pip3."
		
		echo "downloading latest bandit archive..."
		URL="https://github.com/pycqa/bandit/archive/refs/heads/master.zip"
		
		mkdir temp
		pushd temp
		
		if command -v wget &> /dev/null; then
			wget -O master.zip "${URL}"
		elif command -v curl &> /dev/null; then
			curl -o master.zip "${URL}"
		else
			echo "Neither wget or curl were found on your system. Please install one and try again"
			exit 1
		fi
		
		echo "preparing the virtual environment..."
		python3 -m pip install --upgrade pip
		python3 -m pip install --upgrade virtualenv
		python3 -m virtualenv venv
		
		# And activate it:
		source venv/bin/activate
		
		python3 -m pip install bandit pyinstaller

		
		echo "unpacking bandit archive..."
		unzip master.zip

		echo "creating bandit.exe..."
		pyinstaller --noconfirm --onefile --hidden-import pep517 --collect-all bandit bandit-master/bandit/__main__.py
		popd

		mv temp/dist/__main__ bandit-linux

		deactivate
		
		echo deleting temporary files...
		rm -r temp
				
		exit 1
		
	else	
		echo "ERROR: could not find pip3. install python3-pip package"
		exit 1
	fi
	
else 
	echo "ERROR: could not find python3"
	exit 1
fi

