# Beskrivning av sångbokens XML-format

Sångboken är en lista på sånger i XML-format. Just nu heter den _main.xml_, eftersom det kan finnas
flera sångböcker i framtiden, men alltid en förvald som fungerar som något slags halvofficiell
version.

Här beskrivs formatet för att underlätta för dig som vill göra ändringar eller tillägg i sångboken.
Det är viktigt att hålla sig till formatet, annars kommer filen inte att kunna tolkas av appen.

## songs

Det yttersta elementet heter alltid `<songs>`. Det får bara finnas ett element ytterst i hierarkin,
annars validerar inte filen som giltig XML.

`<songs>` kan ha följande attribut:

* `description`: En kortfattad beskrivning av denna sångbok.
* `updated`: Datum då innehållet senast uppdaterades. Skall vara i ett format som följer ISO-8601
  (_YYYY-MM-DD_ rekommenderas).

## song

Songs-elementet kan innehålla ett godtyckligt antal `<song>`-element. Vart och ett representerar en
sång i sångboken.

`<song>` kan ha följande attribut:

* `author`: Sångens textförfattare eller ursprung. Håll texten i detta fält kort.
* `category`: __Obligatoriskt!__ Den kategori som sången tillhör. Se nedan för information.
* `composer`: Sångens kompositör, om någon annan än textförfattaren. Håll texten i detta fält kort.
* `melody`: Melodin som sången skall sjungas till, om den har ett känt namn.
* `name`: __Obligatoriskt!__ Sångens namn, så officiellt och vedertaget som möjligt.

Sångtexten anges som text innanför song-elementet. För att formatera texten snyggt kan följande
element användas. Text som ligger direkt inom song-elementet hanteras som om den låg inuti
ett `<p>`-element.

#### p
En paragraf. Används normalt för att särskilja sångens verser men kan användas för vad som helst
som ska ha lite mellanrum runt sig. Skrivs ut i normalt typsnitt.

#### comment
En kommentar. Används för instruktioner eller noteringar som inte tillhör sångtexten, eller som
ska hanteras särskilt (t ex om något ska talas istället för att sjungas). Skrivs ut i kursiverat
typsnitt.

#### header
En rubrik. Används för rubriker inuti en sång, t ex när en extravers har ett välkänt namn.
Skrivs ut i fetstil.

### Sångkategorier

Varje sång i sångboken tillhör en kategori. Syftet med kategorier är att underlätta sortering och
urval, när man snabbt behöver en sång om exempelvis punsch kan det vara svårt att komma på ett
sångnamn.

Kategorin anges av attributet `category` och kan egentligen vara vad som helst, men
eftersom det är ganska onödigt med kategorier som bara innehåller någon enstaka sång så bör du
välja en av de befintliga. Dessa är:

* _Gasque_: Allmänna sånger som inte hör till någon speciell dryck.
* _Öhl_: Sånger som passar att dricka öhl till (cider räknas som öhl i detta fall).
* _Wihn_: Sånger som passar att dricka wihn till.
* _Brännvin_: Sånger som passar till nubben.
* _Punsch_: Sånger som passar till punschen (alla avec räknas som punsch i detta fall).
* _Utländskt_: Sånger på språk andra än svenska. Bör fortfarande vara relevanta på gasque.
* _Nördigt_: Sånger som handlar om nördiga ämnen, såsom matematik och operativsystem.
* _Esoterica_: Sånger och gyckel som kanske inte sjungs så ofta men som befunnits viktiga att
  bevara för eftervärlden, om så bara att skratta åt.
* _Högtidligt_: Sånger att sjunga med vördnad och andakt. Kanske men inte nödvändigtvis extra
  lämpliga att sjunga på finsittningar.

