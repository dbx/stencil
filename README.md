# Dokumentum előállító modul

Tesztek statusza: <a href="http://ci.dbx.hu/job/stencil-unit-tests/lastCompletedBuild/testReport/"><img src="http://ci.dbx.hu/buildStatus/icon?job=stencil-unit-tests"/></a>

## Függőségek

- Java 8 (tesztelve: `Java 1.8.0_151 OpenJDK 64-Bit Server VM`)
- Fejlesztéshez: Leiningen 2.0.0 vagy újabb (tesztelve: `2.7.1`)
- Futtatáshoz: LibreOffice 5 (tesztelve: `5.3.7.2.0+` verzió)

## Dokumentáció

- [Fejlesztői dokumentáció](development.md)
- [Sablonfájlok nyelvtana](doc/Syntax.md)
- Javadoc: futtassuk a `javadoc.sh` parancsot. A kimenet a `./javadoc` mappaba kerul.
- Generalt javadoc a dbx ci szerveren: http://ci.dbx.hu/job/stencil-unit-tests/Javadoc/

## Fejlesztői konvenciók

A következő konvenciók szerint rendezzük a kódot. 

- [Java Style Guide](http://cr.openjdk.java.net/~alundblad/styleguide/index-v6.html)
- [Clojure Style Guide](https://github.com/bbatsov/clojure-style-guide)
- Javadoc írása kötelező.
- Egységteszt írása kötelező.
