
# v-pipe

Das V&S-Pipeline-Werkzeug macht ein Projekt-Portfolio    
sichtbar. Wie das geht? Siehe Teaser-Video.   

[Alle Featueres auf einen Blick - die veraltete Intro-Video-Serie](https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)  


V-pipe liest einfache Daten-Dateien mit Projekt-Daten  
und Abteilungskapazitäten ein und erzeugt eine  
Übersicht der Belastung aller beteiligten Abteilungen.  
Es gibt die Möglichkeit, Projekte per Pfeiltasten  
wochenweise zu verschieben und die Auswirkungen  
in Echtzeit zu sehen. Alle Tasten-Kürzel  
sind in der Kopfzeile von v-pipe zu sehen.
Z. B. 
- n = now 
- d = Details 
- Pfeil = Cursor bewegen
- Shift-Pfeil = Projekt bewegen


[Quickstart-Videos zu allen Features?  
Ja, gibt's: Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #


## Installation
Die Distribution ist ein Ordner mit dem Namen: `v-pipe-OS/v-pipe-major.minor.bugfix-ReleaseName`.    
Beispiel: v-pipe-macos/v-pipe-1.5.0-average-load
Diesen **Ordner einfach komplett kopieren** - z. B. in das lokale Verzeichnis:  
`c:\v-pipe-1.5.0-average-load`. Aber auch gerne auf einen Memory-Stick oder ins eigene Home.  
Oder nach `Programme` (als admin). Tut auch.  

Releases finden sich alle dort: [v-pipe Releases](https://www.dropbox.com/sh/0ns0bkkz7g2dua2/AABMMgMW0crOoZYfQcOEu30Sa?dl=0)  

[Video-Anleitung 'Installation', Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #

Wenn die Dateien kopiert sind, befinden sich in ihrem Installationsverzeichnis  
auf ihrem Rechner (also z.B. in C:\v-pipe-2.2.0-update-by-id)  
folgende Verzeichnisse und Dateien:

- `Readme.md` (diese Betriebsanleitung als Markdown - falls offline)  
- `v-pipe-macos.sh.command` (macOS-Starter - Doppelklick)
- `v-pipe-linux.sh` (Linux-Starter - in Terminal starten)
- `v-pipe-win.bat` (Windows-Starter - Doppelklick)
- `lib` (ein paar Java-Bibliotheken. Finger weg ;-))  
- `jre` (eine Java-Laufzeit-Umgebung. Nicht anfassen...)
- `bsp-daten` (Unterordner sind gefüllt mit Daten-Dateien als funktionierende Beispiele)
    - Grunddaten
        - `Projekt-Start-End-Abt-Kapa.txt` (ERFORDERLICH: Alle Tasks)
        - `Abteilungs-Kapazitaets-Angebot.txt` (OPTIONAL: Kapa Abteilungen)
        - `Integrations-Phasen.txt` (OPTIONAL: Integrationsphasen der Projekte)
    - Vorlagen
        - `Vorlagen-Projekt-Start-End-Abt-Kapa.txt` (OPTIONAL: Alle Vorlagen, wie Tasks)
        - `Vorlagen-Integrations-Phasen.txt` (OPTIONAL: Integrationsphasen der Vorlagen  
          nur erforderlich, wenn: Vorlagen-Projekt-Start-End-Abt-Kapa.txt UND Integrations-Phasen.txt 
    - Szenarien        
        - `Projekt-Verschiebung.txt` (OPTIONAL: Projekte schieben)
        - `Szenario-Kopie-Original-Verschiebung.txt` (OPTIONAL: Projekte kopieren+schieben)
  
## arbeiten mit v-pipe

### Arbeiten in einem Verzeichnis mit vielen Daten-Dateien
Bei der Arbeit mit Powerpoint oder Word öffnet man jeweils eine Daten-Datei.  
V-pipe liest mehrere Dateien aus einem Verzeichnis. Beim Öffnen von Daten  
wählt man also keine Datei aus, sondern ein Verzeichnis.   
Gleiches gilt beim Speichern.
Die Funktion "Speichern unter..." macht es notwendig, ein neues Datenverzeichnis  
mit dem neuen Namen zu erstellen.
Warum ist das so? Alle Dateien sind Text-Dateien und können auch von Hand  
bearbeitet werden.  

['Erste Schritte', Video-Anleitung, Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #


### Start von v-pipe, wenn die Daten fehlerhaft sind

Jetzt kann das Fenster zur Seite geschoben werden.  
Wann immer die Grunddaten neu erzeugt, verändert oder  
gelöscht werden, rechnet v-pipe und erzeugt  
die Ergebnis-Dateien neu - oder zeigt Datenfehler an.  

['fehlerhafte Daten', Video-Anleitung, Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #



# Daten-Dateien
v-pipe kommt mit wenigen Grunddaten aus. Alles startet mit einfachen  
Projektdaten in einem Textfile 'Projekt-Start-End-Abt-Kapa.txt'.  
Dort finden sich tabellarisch folgende Daten:  
`Projekt-Name Task-Start Task-Ende Kapa-Bedarf auf-Ressource optionaler-Kommentar`  
Alle anderen Daten sind optional und für die Funktion von v-pipe nicht erforderlich.

## Grunddaten 
(Projekte, Kapa-Angebot, Integrations-Phasen)
## `Projekt-Start-End-Abt-Kapa.txt`  
**Grunddaten (erforderlich):** alle Tasks aller Projekte  
Enthält beispielsweise:   
`Projekt-1-Neu-Ulm 20.05.2020 31.5.2020 Abt3   45.6`  
`Projekt-1-Neu-Ulm 12.06.2020 31.6.2020 Abt4   45.6`  
`Projekt-3         15.06.2020 19.6.2020 Abt3.3 45.6`  
`P2                20.04.2020 12.6.2020 Abt3    5.6`  
`P2                20.06.2020 31.7.2020 Abt3    4`  

**Bedeutung:** Datei enthält alle Projekte mit allen Tasks,  
deren Zuordnung zu Abteilungen und die  
notwendige Kapazität als 'Komma-Zahl mit PUNKT' - z.B.: 14.2  
Komma, Strichpunkt, Tab, Leerzeichen sind Trennzeichen.  
KEINE Leerzeichen in den Projekt- und Abteilungs-Bezeichnungen.  
Falls dort welche sind: Ersetzen mit Unterstrich = _.  

['Projekt-Start-End-Abt-Kapa.txt', Video-Anleitung, Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #



## `Integrations-Phasen.txt`  
**Grunddaten (optional):** Integrations-Phasen aller Projekte für Staffelung  
Enthält beispielsweise:  
`6`  
`p1 15.1.2020 18.1.2020 2`  
`p2 30.3.2020  1.4.2020 1`  
**Bedeutung:** Die erste Zeile stellt die Anzahl der Slots  
in der Integrationsphase dar. Die Integrationsphasen der Projekte p1, p2 etc. sind  
untereinander aufgeführt. Die Zahl am Ende jeder Zeile ist der Bedarf an Slots.  
Dieser ist in der Regel 1 und maximal gleich der Anzahl der verfügbaren Slots -   
im Beispiel sind das 6.  

['Integrations-Phasen.txt', Video-Anleitung, Bitte hier lang...(https://loom.com/share/folder/098a2ada42f647bfbbcc89e4d0e4a202)]: #

## `Abteilungs-Kapazitäts-Angebot.txt`
**Grunddaten (optional):** Kapazitäts-Angebot aller Abteilungen für Auswertung  
Enthält beispielsweise:  

     {
       "Kapa_Gesamt": {
         "Feiertage": ["1.1.2020", "1.6.2020"],
         "Kapa_Profil": {
            "2020-23": 80, // in Prozent!
            "2020-24": 60,
            "2020-25": 80
         }
       },
   
       "Abteilungen": {
        
         "Konstruktion": {
            "Kapa": {
               "gelb": 140,
               "rot": 190
            },
            "Kapa_Profil": {
               "2020-23": 100,
               "2020-24": { "gelb": 170, "rot": 250 },
               "2020-40": { "gelb": 150, "rot": 200 },
            }
        },

        "Montage": {
           "Kapa": {
              "gelb": 240,
              "rot": 490
           },
           "Kapa_Profil": {
           }
        },

        "IBN": {
           "Kapa": {
              "gelb": 340,
              "rot": 500
           }
        }
    }

JSON ist möglich. YAML ist ebenfalls möglich:

```
  Feiertage:
  - "1.1.2020"
  - "10.04.2020"
  - "13.04.2020"
  - "1.5.2020"
  - "21.5.2020"
  - "1.6.2020"
  - "3.10.2020"
  - "25.12.2020"
  Kapa_Profil:
    "2020-W21": 50
    "2020-W22": 50
Kapa_Abteilungen:
  M-Kon:
    Kapa:
      gelb: 200
      rot: 400
    Kapa_Profil:
      "2020-W01": 100
      "2020-W02": 100
      "2020-W34":
        gelb: 120
        rot: 150
      "2020-W35":
        gelb: 200
        rot: 400
  E-Kon:
    Kapa:
      gelb: 160
      rot: 200
    Kapa_Profil: {}
  SW:
    Kapa:
      gelb: 340
      rot: 460
  Mon:
    Kapa:
      gelb: 160
      rot: 400
  IBN:
    Kapa:
      gelb: 100
      rot: 200

```

**Bedeutung:** der Abschnitt `Kapa_Gesamt` beinhaltet Feiertage und ein Kapa_Profil  
in Prozent. Die Prozent-Angaben gelten jeweils genau für diese eine Woche und alle Abteilungen.  
Der Abschnitt `Abteilungen` beinhaltet Abteilungsnamen und  
darunter jeweils zwei Infos:  
`Kapa`: rote und gelbe Kapazitätsgrenze. Diese Einteilung dient der   
Visualisierung von grünem, gelbem und rotem Bereich. Die grün->gelbe Grenze entpricht 100%.  
`Kapa_Profil`: Dort kann das Kapa-Profil der Gesamt-Firma  
(in KWs und Prozent) überschrieben werden. Wenn also im Gesamtprofil 50% 
für 4 Wochen im Sommer eingetragen ist, dann kann das für die Produktion trotzdem überschrieben   
werden. Bei einem Eisproduzenten würde für die Sommer-Monate vielleicht die halbe Firma Urlaub
machen (50%) in der Produktion wären aber extrem viele Leihkräfte da (150%).
Kapazitäts-Sprünge werden so beschrieben:  
`"2020-24": { "gelb": 140, "rot": 250 }`  
Sie gelten nicht **für** sondern **ab** der angegebenen KW.
Sie werden "multipliziert" mit den Gesamt-Prozent-Wochen-Werten - falls es diese gibt. 
Wenn es Abteilungs-spezifische Prozent-Wochen-Werte gibt, dann werden die globalen 
Prozent-Werte dieser Wochen ignoriert.  
Das Kapa_Profil ist optional.  
Die Syntax der Datei ist JSON oder Yaml.  

## Vorlagen
## `Vorlagen-Projekt-Start-End-Abt-Kapa.txt`
Exact wie `Vorlagen-Projekt-Start-End-Abt-Kapa.txt`...  
nur dass es sich dabei um Vorlagen handelt und nicht um Projekte.   

## `Vorlagen-Integrations-Phasen.txt`  
p1 15.1.2020 18.1.2020 2  
p2 30.3.2020 1.4.2020 1  
Wie `Integrations-Phasen.txt`...  
Mit zwei Unterschieden:
1.) die erste Zeile enthält NICHT die Pipeline-Kapazität.
2.) die Integrationsphasen beziehen sich auf die Vorlagen - nicht auf die Projekte.

## Szenarien
## `Projekt-Verschiebung.txt`  
**Szenario (optional):** Verschiebung aller Tasks einzelner Projekte  
Enthält beispielsweise:  
`P2 14`  
`Projekt1 -21`  

**Bedeutung:** Datei enthält die Verschiebung der Projekte in Tagen.  
Es müssen nicht alle Projekte aufgeführt sein.   
Nur die Verschobenen.  
`+` bedeutet: Verscheibung in die Zukunft.  
P2 wird um 2 Wochen geschoben.  
Der 31.7.2020 wird duch `14` zum 14.8.2020  
Projekt-1-Neu-Ulm wird durch `-21` vorgezogen:   
Aus 31.6.2020 wird 10.6.2020.  


## `Szenario-Kopie-Original-Verschiebung.txt`
hies vor Version 1.2.0 `Template-Original-Verschiebung.txt`  
**Szeanrio (optional)** Kopie von Projekten  
Enthält beispielsweise:  
`pKopie P2 -12`    

**Bedeutung:** Datei enthält Kopier-Anweisungen.  
`+` bedeutet: Verscheibung in die Zukunft.  
pKopie wird um -12 Tage geschoben. Und zwar nachdem  
P2 durch die ursprüngliche Verschiebung +14 Tage verschoben wurde.  
Der 14.8.2020 wird für pKopie duch `-12` zum 2.7.2020  
Die Verschiebung durch `Projekt-Verschiebung.txt` wird  
zuerst angewandt. Erst dann kommt diese hier dazu!

### Backup - kein Überschreiben von Dateien beim Speichern  
Dateien werden nie einfach überschrieben.  
Wann immer eine Datei schon existiert,  
wird ein Backup mit Datum und Uhrzeit erstellt.  
Beim Speichern wird ein Ordner mit dem Namen backup angelegt.  
Dort wird ein Ordner mit einem Zeit-Stempel angelegt.  
Dorthin werden die aktuellen Dateien verschoben,  
die beim Speichern 'überschrieben' werden.   

### kein Undo - aber AutoSave und Backup
Es gibt kein Undo. Allerdings gibt es im Datei-Menü eine Autosave-Option.
Alle 10 Sekunden wird überprüft, ob das Modell sich verändert hat.   
Falls ja, wird es gespeichert.  
Bei jedem Auto-Save wird im backup-Ordner eine Sicherung angelegt,  
die einfach wie jedes andere Modell geöffnet werden kann.


### Terminfester 
Alle Termin-Fenster-Angaben sind hinten offene Intervalle.  
D.h. der letzte Tag des Intervalls gehört nicht zum Intervall dazu.  
Die zwei Intervalle  
20.10.2020 25.10.2020  
und  
25.10.2020 30.10.2020  
überlappen keinen Augenblick! Das erste endet im letzten Augenblick  
VOR dem 25.10.2020. Insofern reicht es genau bis zum 25.10.  
Das zweite beginnt mit dem ersten Augenblick des 25.  
D.h. Das erste Intervall besteht aus den ganzen Tagen:  
20 21 22 23 24 - also 5 Tage. Das zweite aus 25 26 27 28 29.  
Beide Intervalle hintereinander umfassen 10 kontinuierliche Tage.  
Der 20.10. gehört dazu. Der 30.10. nicht!

## Update des Modells auf Basis von Update-Daten
Projekte verändern sich im Laufe der Zeit.  
Zeitschätzungen werden korrigiert.  
Tasks kommen hinzu oder werden abgearbeitet.  
Ein v-pipe Modell kann "auf Basis von Daten aktuell gehalten werden".
Dafür gibt es das Menü "Werkzeug->Aktualisierung für Projekte einlesen".
Damit Daten eingelesen werden, muss im Unterordner `read-updates-from-here`  
des aktuellen Modell-Verzeichnisses folgende Datei liegen: 
`Update__Projekt-Start-End-Abt-Kapa.txt`  
Also so:  
`current-folder/read-updates-from-here/Update__Projekt-Start-End-Abt-Kapa.txt`    
Wird diese Datei erfolgreich gelesen, dann landet sie  
mit Zeitstempel im 'done'-Verzeichnis:  
`current-folder/read-updates-from-here/**done**/Update__Projekt-Start-End-Abt-Kapa-2022-04-17 18.16.10.097.txt`

### ganze Projekte aktualisieren
Wenn die Datei `Update__Projekt-Start-End-Abt-Kapa.txt`  
keine Identifikation / Kennzeichnung  
(das Kommentarfeld beginnt mit nicht mit ID:)  
am Ende jeder Zeile enthält,
dann werden die Projekte im Modell vor dem Update vollständig
gelöscht. D.h. die Strategie des Updates lautet:
1. Stelle sicher, dass kein Task-Kommentar-Feld in den Update-Daten mit ID: beginnt.
2. Stelle sicher, dass kein Task-Kommentar-Feld im Modell mit ID: beginnt.
3. lese alle Projekte in den Update-Dateien
4. lösche alle Projekte im Modell, die in den Update-Daten gefunden wurde.
5. Ersetzte die gelöschten Projekte vollständig durch die Update-Daten.

### einzelne Tasks aktualisieren

Jeder Task innerhalb eines Projektes lässt sich mit Hilfe des  
Kommentar-Feldes am Ende der Daten in  
`Projekt-Start-End-Abt-Kapa.txt` eindeutig kennzeichnen.
(das Kommentarfeld beginnt dann mit ID:, die Kennzeichnung
schießt sich ohne Leerzeichen an, z.B.: ID:4711).  

Liegt diese Form der eindeutigen Kennzeichnung in der Update-Datei  
`Update__Projekt-Start-End-Abt-Kapa.txt` vor, dann  
werden die 'passenden' Tasks im Modell überschrieben.  
Falls im Update Kennzeichnungen vorhanden sind, die  
im Modell nicht vorliegen, werden diese Tasks neu angelegt.  
Falls im Modell Tasks vorliegen, die **nicht** mit einer ID:  
gekennzeichnet sind, werden diese **gelöscht**.
Zum Löschen gekennzeichneter Tasks durch die Update-Daten  
ist das Feld Kapa-Bedarf auf 0 zu setzen.  
D.h. die Strategie des Updates lautet:
1. Nur wenn ALLE Task-Kommentar-Felder in den Update-Daten mit ID: beginnen, dann...
2. lese alle Tasks in den Update-Dateien
3. finde die zugehörigen Tasks in den Projekten und ersetze sie.
4. falls in den Update-Daten der Kapa-Bedarf auf 0 gesetzt wurde: Lösche den Projekttask (oder ignoriere die Update-Daten, falls sich keiner findet)
5. falls in den Projekten Tasks existieren, die keine Kennzeichnung tragen: lösche alle.
6. falls Projekte dann keine Tasks mehr haben, lösche die kompletten Projekte.
7. falls Tasks im Update eine Kennzeichnung tragen, die es im Projekt nicht gibt, lege sie an.
8. falls es Tasks mit Projekt-Namen im Update gibt, für die es kein Projekt im Modell gibt: lege das Projekt an.
9. für Projekte, die verschoben werden, bleibt IP und Liefertermin stabil! 

### Plugin zur Transformation der Update-Daten vor dem Einlesen
Angenommen, die Update-Daten liegen als Excel-Datei vor.
Aus den Excel-Daten müssen vor jedem Update die Daten in die Textdatei  
`Update__Projekt-Start-End-Abt-Kapa.txt`  
überführt werden. Um das zu automatisieren gibt es ein Skript:  
`current-folder/read-updates-from-here/plugin/import.groovy`  
Wird diese Datei gefunden, dann wird der Inhalt als groovy-script ausgeführt.
Ein Beispiel findet sich Ordner  
`bsp-daten/bsp-11-update-with-plugin`  
Falls ein Plugin mit dem Namen import.groovy existiert,  
dann wird es vor jedem Einlesen der Updates durchgeführt.  
Vorsicht: **alle** Dateien aus dem Ordner `current-folder/read-updates-from-here/`  
werden verschoben nach `done`.  
Vor dem Import also alle offenen Dateien schließen,  
sonst gibt es eine (je nach macOS, win, linux) eine Fehlermeldung  
oder die offene Datei wird nach dem Update aus z.B. Excel versehentlich nochmal gespeichert.

## mit Vorlagen arbeiten
Eine Vorlage besteht aus Tasks. Auch für die Integrationsphasen kann  
es Vorlagen geben - muss aber nicht.
Vorlagen sind ganz ähnlich wie Projekte. Der einzige Unterschied:  
Sie haben keinen Liefertermin. Erst wenn aus einer Vorlage ein  
Projekt erzeugt wird, erhält das Projekt einen Liefertermin und  
wird 'dorthin geschoben'.

Damit Vorlagen im Editor von den existierenden Projekten  
abgeleitet werden können, gibt es einen einfachen Kniff:
Vorlagen und Projekte lassen sich "tauschen".
Jetzt erscheinen alle Vorlagen als Projekte und alle Projekte als Vorlagen.
Wenn jetzt also ein "Projekt" verändert wird, dann ändert man eigentlich eine Vorlage.
Wenn jetzt eine Projekt aus einer Vorlage erzeugt, dann erzeugt man eigentlich eine Vorlage aus einem Projekt.
Wenn schließlich alle Vorlagen bearbeitet sind, dann wechselt man wieder.
Während des "Vorlagen-Modus" kann man das Modell nicht speichern.
D.h. der AutoSave-Modus wird automatisch deaktiviert.

## Release History

### 2022-08-12 `2.2.0-update-by-id`
- release and starter for win, linux and macos
- error checking mode - work on the data-files and see error messages in realtime in  gui
- plugin for getting data transformed (e.g out of excel), when reading delta-data
- merging updates based on ID: tags residing in comment field of tasks

### 2022-04-17 `2.1.0-copy-and-update`
- have a directory scanned for delta-data in order to update the model.
- copy a project (not from template - just plain copy)

### 2022-04-16 `2.0.0-all-in-gui`
- linux, windows and mac
- month load
- project comments/history

### 2022-03-16 `1.9.0-tidy-code`
- moved and renamed many classes / packages for more clarity

### 2022-03-14 `1.8.0-beta-all-gui`
- everything can be changed in gui 

### 2022-03-12 `1.7.0-ip-in-gui`
- pipeline (integration phases) can be created / deleted / hidden

### 2022-03-12 `1.6.0-ipco-pax-edition`
- can only open valid data folders
- simplified creation of capacity model
- can create new model from GUI

### 2022-02-27 `1.5.0-average-load`
- show load average (5 weeks moving average) by pressing 'a'
- created starter for dropbox

### 2022-02-13 `1.4.4-delivery-date-macos`
- create delivery date from last task, if not available
- save, change and visualize delivery date 

### 2021-08-26 `1.3.0-Create-Project`
- create new Project from "nothing"
- show task description in project tooltip

### 2021-02-07 `1.2.0-Szenario-Templates`
- can model and use templates (Vorlagen)
- delete projects in Detail-View (delete-Button)
- vpipeGui does not open current working directory at startup any more 
- fixed bug: caching problem with recalc of week-patterns and capa-model after model reloading fixed 

### 2020-11-03 `1.1.0-Szenario-Differenzen`
- compare files

### 2020-10-30 `1.0.3-Details-Editor-Bugfixes`
- increase / decrease tasks with CTRL-arrow (--> = inc, <-- = dec)

### 2020-10-20 `1.0.2-Details-Editor-Bugfixes`
- Datenkonsistenz-Prüfung Abteilungen geht wieder

### 2020-10-19 `1.0.1-Details-Editor-Bugfixes`
- duplizieren
- Änderungen annehmen
- CTRL-Pfeil vergrößert / verkleinert die Projekt-Tasks
- Löschen des letzten Task in Detailansicht nicht möglich

### 2020-10-19 `1.0.0-RELEASE-Projekt-Editor`
- Editor für Projekt-Daten (Alle Felder bearbeiten, Copy und Cut von Tasks)
- Reihenfolge der Tasks in der Projektansicht wie in Abteilungs-Kapa-Angebot, falls vorhanden  

### 2020-09-16 `0.9.13-Beta-3-Bugfixes`  
- Reihenfolge der Projekttasks nach End-Termin
- Reihenfolge der Auslastung wie in Abteilungs-Kapa-Angebot, falls vorhanden  
- alle Angaben in Auslastungs-Tooltip auf eine Stelle nach Komma runden

### 2020-09-15 `0.9.12-Beta-2-Bugfixes`
- Kapabedarf der Tasks und Projekte sichtbar  

### 2020-08-30 `0.9.11-Beta-1-Bugfixes`
- Cursor sprang 

### 2020-08-29 `0.9.10-LoadSomeDetails`
- drei Modi der Tooltips im Auslastungsfenster: no, some, details
- vollständige Synchronistation der Scrollbars (Staffelung - Auslastung)
- Cursor mit Fadenkreuz in der Staffelung und Projektsicht
 
### 2020-08-07 `0.9.7-ScrollCenter`
- zentrieren beim Scrollen mit Maus: Element des Mouse-Cursors bleibt sichtbar
- zentrieren beim Scrollen mit +/-: Tasten-Cursor bleibt sichtbar

### 2020-08-07 `0.9.6-ScrollNice`
- Scrollt zu 'jetzt' beim öffnen
- sprint nicht mehr beim umschalten der Detail-Tooltips
- Bug bei Anzeige der Skalierung der Kapa-Belastung gefixt
- mit 'n' kann man den Cursor zu 'jetzt' fahren (_n_ow)

### 2020-07-09 `0.9.5-BetaPhase-Bug`
- Sort ToolTip + 2-Stellen-Jahres-Bug raus

### 2020-07-08 `0.9.4-BetaPhaseNice`
- Markierungen, Doku

### 2020-07-06 `0.9.3-BetaPhase`
- minor changes like examples, docu, marker, fucus by tab and mouse, mouse-cursor, tooltips (d)
 
### 2020-06-30 `0.9.1-TePiLoSa`
- Pipeline-View: Verschieben auch ohne piplining Daten crasht nicht mehr

### 2020-06-30 `0.9.0-TePiLoSa`
- visuelles Pipelining

### 2020-06-21 `0.8.0-LoadSave-Template`
- Speichern der Daten (in eigenem Daten-Ordner) - öffnen von Datenordnern
- Szeanrien im Portfolio auf Basis von Templates

### 2020-06-14 `0.7.0-GUI-Project-Editor`
- Tasks eines Projektes auch verschieben.

### 2020-06-01 `0.6.0-GUI-Project-View`
- Tasks eines Projektes visualisieren (noch nicht verschieben).

### 2020-05-25 `0.5.0-GUI-Project`
- Belastung durch ein Projekt visualisieren.

### 2020-05-23 `0.4.0-GUI-Pipeliner`
- Pipeline visualisieren, editieren.

### 2020-05-13 `0.3.1-Pipeliner`
- Performance besser beim schreiben  
 (größer 40s --> deutlich kleiner 10s mit Ruhlamat-Daten)

### 2020-05-10 `0.3.0-Pipeliner`
- Pipeliner - lesen von Integrations-Phasen.txt
- Browser mit Hilfe nur beim ersten Start
- Beispiel-Daten in Ordner bsp-daten, damit Update keine Daten zerstört

### 2020-05-07 `0.2.0-Monats-Belastung`
- Option -s startet im Single-Modus = kein Deamon.  
Nur ein Durchlauf.
- Option -m startet im Multi-Instance-Modus.  
Es können mehrere v-pipes gleichzeitig laufen.  
Das ist nur vernünftig in verschiedenen Verzeichnissen!  
- beim öffnen von v-pipe im deamon-mode geht beim Start ein Browser mit Hilfe auf   
(essentials zu den Daten-Formaten)
- Monats-Auswertung auch... Abteilungs-Kapazitäts-Belastung-Woche.txt und -Monat.txt
- Abteilungs-Kapazitäts-Belastung.txt enthält immer die aktuelle Ergebnisse.  
 Falls Überschreiben droht wird vor dem Überschreiben eine Backup-Datei erzeugt.

### 2020-05-06 `0.1.0-Vor-Ruhlamat`  
- Readme.md :-)
- Beispiel-Dateien.
- Lesen von Projekt-Verschiebung.txt  
- Automatisches Neu-Generieren der Ergebnisse, wenn Datendateien sich ändern.   

### 2020-04-25 `0.0`
- Lesen von Projekt-Start-End-Abt-Kapa.txt   
- Schreiben von Department-Load-Result.txt

## Ideen und zukünftige Features 
Das hier ist keine Roadmap, sondern eine Ideen-Sammlung.  
Nützlichkeit in absteigender Reihenfolge:
- Automatisches Pipelining (entweder an der Pipeline oder an einer Abteilung)
- Monte-Carlo Simulation für "wenns gut läuft / wenns schlecht läuft"
- settings file für user: aktuelles Verzeichnis, details, offene Fenster, Window-Size, Splitter, ...
- Finanz-Zahlen und damit Durchsatz-Kennzahlen
- adapter elegantly import project data
- backlog of department for X weeks (in order to simulate "additional work")
- AutoSave bei jedem "setDirty - in eigenem Thread auf einer Kopie des Modells" - damit gäbe es ein UNDO.

### bekannte BUGS
**noch nicht gefixt**
- macOS: Schließen mit dem System-Menu oder Command-Q ohne Abfrage nach Speichern. May be fixed [with this hint](https://alvinalexander.com/java/java-mac-osx-about-preferences-quit-application-adapter/)  
- macOS: two finger zoom and scroll does not work. [maybe this helps](https://github.com/mcourteaux/MultiTouch-Gestures-Java)  
- beim Öffnen eines fehlerhaften Modells bricht der Korrektur-Modus ab und verlangt speichern

**gefixt**
- macOS: two-finger-zoom lead to java.lang.NoSuchMethodError: handleGestureFromNative  
Fix: used java11.0.15

## Feedback
an: benno.loeffler AT gmx.de