# gpt-vunds

examples for openai api usage in clojure.
- simple prompts.  
- enhanced prompts.   
- very simplistic local embedding db.
- streaming answers word by word

the most interesting part is not to run the code  
but to have a look to `src/gpt-vunds/tutorial.clj` and get and  
understanding of how it works.  
Start a repl and have fun.

But if you want to run it:  
install and run it...

## Installation

```
git clone https://github.com/bennoloeffler/gpt-vunds.git
lein uberjar
chmod u+x ./gpt-vunds
```
then put the folder of the script 
```
./gpt-vunds
```
into the path. E.g. put the following line into your .bashrc
```
export PATH=$HOME/projects/gpt-vunds:$PATH
```

## Usage

    $ java -jar target/uberjar/gpt-vunds-0.1.0-SNAPSHOT-standalone.jar books
or short
```
$ gpt-vunds books
``` 

You will get a list of famous books.
Then have a look at those example files:

```
books--instructions.txt  
books--examples.txt  
books--prompt.txt  
```
You may also ignore the books--prompt.txt
and provide your question as parameter:

    $ gpt-vunds books "provide 3 famous autobiographies of german business people" 

So basically, you need to write 2 files to create a specific prompt:  
```
xxx--instructions.txt  
xxx--examples.txt
```  
you may optionally write your specific prompt data into  
```
xxx--prompt.txt
```
but you may override xxx-prompt.txt by a parameter

    $ gpt-vunds xxx "here is the specific prompt that uses xxx--instructions.txt and xxx--examples.txt" 

As soon as you need to put a massive amount of data to the prompt,  
use the file xxx-prompt.txt,
Have a look at the example:
```
transform-data--prompt.txt
transform-data--instructions.txt
transform-data--examples.txt
```
## TODOs
- function callings:  
  https://semaphoreci.com/blog/function-calling  
  https://platform.openai.com/docs/guides/function-calling/function-calling
- assistants:  
  https://platform.openai.com/docs/api-reference/assistants
- exlore alternatives:  
  https://cohere.com/

## License
http://www.wtfpl.net/

DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
Version 2, December 2004

Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

Everyone is permitted to copy and distribute verbatim or modified
copies of this license document, and changing it is allowed as long
as the name is changed.

DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 

TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

0. You just DO WHAT THE FUCK YOU WANT TO.
