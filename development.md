## Fejlesztés

Először is fordítsuk le a projektet.

`$ lein compile`

Mielőtt IDEÁ-ban megnyitjuk a projektet, generáljuk le a `pom.xml` fájlt.

`$ lein with-profile test pom`

Ekkor megjelenik a `pom.xml`, ami alapján már be tudjuk importálni a projektet.

## Teszt futtatás

- A Clojure egységtesztek tesztek futtatása a `$ lein test` paranccsal történik.
- A Java egységtesztek futtatása a `$ mvn test` paranccsal történik.

## Library fordítása

Más alkalmazásban felhasználható JAR fájl a `$ lein uberjar` paranccsal készíthető. Ekkor két JAR fájl jön létre:

- `target/stencil-*.jar`: csak a library kódját tartalmazza.
- `target/stencil-*-standalone.jar`: tartalmaz minden függőséget.

## Javadoc dokumentáció

Futtassuk a `./javadoc.sh` parancsot. A generált dokumentáció a `javadoc` mappába kerül. A dokumentációban az *overview* oldal tartalmaz példa kódot, amely segíti az elindulást.
