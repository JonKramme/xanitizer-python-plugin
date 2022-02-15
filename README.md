# Python Language Plugin
## A plugin for [Xanitizer]

This is a Plugin for [Xanitizer] to support Python code analysis using the third-party tool [Bandit].

## Manual Content

  - Plugin Features
  - Additional repository content
  - Usage
  - Updating
  - Notes


### Plugin Features

- Run [Bandit] code analysis in [Xanitizer] Projects.
- Customize which tests to run in Xanitizer's GUI.


### Additional repository content

- This manual.
- an executable Bandit file (for Windows and Unix) generated from source.
- 'batch' and '.sh' file to generate or update the Bandit executable that the plugin uses.


### Usage
1. Insert plugin.jar file into Folder: "%Xanitizer%\xanitizer-plugins"
2. Insert "bandit-linux" or "bandit.exe" into folder: "%Xanitizer%\xanitizer-plugins\tools"
3. (Re)start Xanitizer

### Updating
- **IMPORTANT**: Before updating the Bandit version please refer to "**Notes**" below.
- Included in the repo is a folder called [buildBandit] which contains scripts to generate a new (up to date) version of the Bandit executable used by the Plugin.
- A '.bat' file for Windows and a '.sh' file for Unix.
- Insert the generated executable files into the "%Xanitizer%\xanitizer-plugins\tools" folder.
- Update test mapping in the plugin source code (please refer to "**Notes**" below.) 

#### Notes

 - Bandit tests are manually "registered" and listed in the plugin. This information was manually pulled from the documentation of the respective Bandit test files.
 - This means: If you update the Bandit version and it includes additional or changed tests, you will have to manually synchronize these changes to reflect them in the plugin code. (Bandit doesn't provide or let you easily scrape this information from the source)
 - CWE Code mapping to Bandit Tests is taken from this PR:
 <https://github.com/PyCQA/bandit/blob/981df3f617d2f05c746ba06e520412c503ecbf80/bandit/core/cwemap.py>


##### Author
    Jonathan Kramme
    10.11.2021

   [bandit]: <https://github.com/PyCQA/bandit>
   [xanitizer]: <https://www.xanitizer.com/xanitizer/>
   [buildbandit]:<https://github.com/RIGS-IT/xanitizer-python-plugin/tree/main/buildBandit>
  
