#### TODOs

- when running, remove the mouse drag on the timer, so that iOS can be moved up/down by drag
- arrows: set the total time - and also the resume time
- when jumping, set full minutes
- when pressed SHIFT, used seconds. Otherwise, do minutes
- build it on repl it with babashka
  https://github.com/kloimhardt/babashka-scittle-guestbook/blob/main/guestbook.cljs
- prevent screensaver: https://www.educative.io/answers/how-to-keep-your-screen-awake-using-javascript
- SPACE does not work on ios <-- --> do not work on ios
- bug: does not play sound on ios, may be solved  
  - by: interaction, when pressing start first time
  - and: https://stackoverflow.com/questions/31776548/why-cant-javascript-play-audio-files-on-iphone-safari
- use arc for visualisation
- macro for rf/reg-sub standard
- syntactic sugar ::<


# wet - web-timer

## deployed here
https://replit.com/@bennoloeffler/timer

## hints for css & sass
https://bulma.io/documentation/customize/with-node-sass/
https://css-tricks.com/styling-cross-browser-compatible-range-inputs-css/

## building
```
npm start (start sass watching and building)
```
```
shadow-clj watch app
```
```
(start) webserver in repl
```
```
lein uberjar
```
## running
To start a web server for the application, run:
```
lein run 
```
or the deployed version   
at [repl.it](https://repl.it)

https://timer.bennoloeffler.repl.co/

