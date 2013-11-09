# Tetris (in ClojureScript)

This is a Tetris clone written using ClojureScript and core.async.

It was originally written for a [Likely](http://likely.co) hack-day,
to investigate using core.async within a ClojureScript app, and to
experiment with design patterns in ClojureScript.

## Design/development rationale

*When I get a moment, I'll write up a longer piece about the various
 design patterns that I've used here - watch this space!*
 
*In the meantime - a lot of the inspiration for the design came from
 David Nolen's [series of blogs](https://swannodette.github.io) on
 ClojureScript and core.async, and conversations with
 [Simon Hicks](https://github.com/simonhicks) - thank you to you
 both!*

## Getting it running:

After you've forked/cloned this repo, you can start Tetris by running:

    lein start
	
This will compile the CLJS files (once) and start a lightweight web server on
http://localhost:3000
	
If you want to hack on Tetris, you can use:

    lein dev

This sets up a `lein cljsbuild auto` process, to automatically
re-compile the CLJS files, the web server, and an nREPL server for the
web-app.

## Health Warning: 

This clone is as addictive as any other Tetris clone. If you do suffer
from symptoms of addiction, please stop playing immediately and seek
medical advice/alternative forms of entertainment. ;)

## Licence

This code is meant to be an educational code base - at the very least
for myself. If it helps others as well - that's great! Feel free to
hack about with it!

I'd be grateful if you'd let me know of any enhancements/suggestions
etc, either by usual GitHub means or by tweeting
[@jarohen](https://twitter.com/jarohen). Thanks!

&copy; James Henderson 2013, all rights reserved.

