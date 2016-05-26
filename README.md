<center>
![yojimbo from Final Fantasy X](http://vignette3.wikia.nocookie.net/finalfantasy/images/0/07/Ffx-yojimbo.jpg/revision/latest?cb=20120109145520)
</center>

# Yojimbo

A toolkit for java developers that are unfamiliar with node.js and want a toolkit for easy web app development.

Yojimbo provides a set of commands:
* __bower:__ dependency management built on top of bower
* __compress:__ javascript compression utilities
* __merge:__ javascript merge utilities
* __serve:__ static http server

# Getting started

Compile yojimbo with maven:

> mvn clean package

# Tools

## Bower
Uses bower dependency management index to download bower dependencies

### Usage

> java -jar yojimbo bower_install

## Compress
Uses yui compress to compress the provided .js file

### Usage

> java -jar yojimbo compress -Dcompress.input=app.js

## Merge
Combines all .js files in `/dev` to `/dist/`
 
### Usage
 
> java -jar yojimbo merge -Dmerge.output=app.js

### Options

* __merge.output:__ the name of the file (optional, defaults to `app.js`)

## Serve
Start a local http server to run the web application

### Usage

> java -jar yojimbo serve -Dserve.port=:port -Dserve.staticFolder=:dir

### Options

* __serve.port:__ the name of the file (optional, defaults to `app.js`)
* __serve.staticFolder:__ the directory from which to serve the web app (optional)
