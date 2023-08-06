IN-sektionens Sångbok
=====================

_This application is currently only available in Swedish. International visitors, please contact
the administrator if you have any questions._

Det här är en mobilapp som låter användaren bläddra i IN-sektionens Sångbok. Den riktar sig främst
till studenter på Kungliga Tekniska Högskolan (KTH) i Kista. Det finns dock inget som hindrar andra
från att använda appen för nytta eller nöje. Syftet är att fungera som en ersättning eller
komplement till den tryckta sångboken.

Både appen och sångernas texter är öppen källkod (samtliga omfattas dock av upphovsrätt). Det betyder
att vem som helst kan hjälpa till att bidra med fler sånger och rättelser.

## Hur du bidrar med sånger eller rättelser
[Källfilen](https://github.com/wsv-accidis/in-sangbok/blob/master/sangbok/main.xml) som sångerna
lagras i finns här på Github. Om du kan redigera XML och behärskar Git, kan du själv göra önskade
ändringar och skapa en Pull request, som kommer att granskas av repots administratör. Se
riktlinjerna nedan!

Om du inte är bekväm med att modifiera källfilen direkt, så kan du skapa en
[Issue](https://github.com/wsv-accidis/in-sangbok/issues) där du beskriver ditt ärende - detta
kräver inga särskilda kunskaper, men du behöver ett Github-konto.

I sista hand kan du kontakta [administratören](https://github.com/wsv-accidis) direkt via e-post.

### Riktlinjer för sångbokens innehåll
* Alla sångtexter skall följa [THS Sångpolicy](http://ths.kth.se/om-ths/styrdokument-och-moteshandlingar/)
  och övriga tillämpliga regelverk.
* Kontrollera stavning och grammatik (sångtexter förväntas naturligtvis inte vara helt grammatiskt
  perfekta, men i möjligaste mån). Det är tråkigt att sjunga sånger med stavfel och särskrivningar.
* Om sångtexten innehåller könade uttryck (herrar/damer, systrar/bröder, osv) se gärna till att
  sången fungerar att sjungas oavsett kön.
* Välj rätt kategori till din sång - ta en av de befintliga, hitta inte på en ny om du inte
  verkligen måste. Om du är osäker, ta "Gasque"!
* Om du ändrar direkt i XML-filen, förändra inte filens formatering (t ex indenteringen). Det gör
  ändringarna svåra att överskåda, och din PR kommer att nekas.
* Om du ändrar direkt i XML-filen, kontrollera att det inte smugit sig in syntaxfel. Du kan
  använda [W3 Markup Validation Service](https://validator.w3.org/) för att enkelt testa din fil.

## Status
Aktuell version är 1.4. Applikationen har publicerats på
[Google Play](https://play.google.com/store/apps/details?id=se.insektionen.songbook).

## Behörigheter
Den här appen kräver följande behörigheter:

* INTERNET - För att hämta sångboken från Internet.

## Licensiering
IN-sångbok distribueras i enlighet med licensvillkoren i **Apache License version 2.0**.

_IN-sångbok is distributed according to the terms of the **Apache License version 2.0**._

## Tillkännagivanden
IN-sångbok använder följande tredjepartsbibliotek och resurser, utöver
utvecklingsverktyg för Android utgivna av Google:

* [OkHttp HTTP & SPDY client for Android and Java](https://github.com/square/okhttp)
* [Material Design Icons från Google](https://github.com/google/material-design-icons)
