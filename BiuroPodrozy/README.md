# Biuro Podróży: opis działania programu

Aplikacja desktopowa w Java Swing, która symuluje wyszukiwarkę wycieczek biura podróży
(w stylu Itaka, TUI, Wakacje.pl). Użytkownik zaczyna na ekranie powitalnym, przechodzi do
wyszukiwarki, wpisuje nazwę i ustawia filtry, a wyniki pojawiają się jako karty jedna pod drugą.
Dane wycieczek są wczytywane z pliku tekstowego.

---

## Uruchomienie

**Z terminala** (z katalogu `BiuroPodrozy/`). `javac` nie kopiuje tłumaczeń ani obrazków,
dlatego robi to `cp`:

```bash
javac -d out $(find src -name '*.java') && cp -r src/i18n src/img out/ && java -cp out TravelAgency
```

**Z VS Code:** konfiguracja „TravelAgency” w `.vscode/launch.json` (F5). Ma ustawione
`"cwd": "${workspaceFolder}/BiuroPodrozy"`. Ścieżka do danych `data/trips.txt` jest względna,
więc program musi startować w katalogu `BiuroPodrozy/`.

Wymagana Java 19+ (`Locale.of`, `record`, `Stream.toList()`).

---

## Struktura projektu

```
BiuroPodrozy/
├── data/
│   └── trips.txt                  dane wycieczek (czytane przy starcie)
└── src/
    ├── TravelAgency.java          main: ustawia Nimbus i tworzy okno na EDT
    ├── model/                     DANE: czym jest wycieczka
    │   ├── Trip.java              wycieczka + walidacja + sampleTrips() (dane awaryjne)
    │   ├── Board.java             enum: wyżywienie
    │   ├── Transport.java         enum: dojazd
    │   └── Amenity.java           enum: udogodnienia
    ├── data/                      PLIK: odczyt i zapis wycieczek
    │   ├── TripRepository.java    load() / save(), parsowanie linii
    │   └── TripParseException.java  własny wyjątek: błąd w konkretnej linii pliku
    ├── filters/                   LOGIKA wyszukiwania (bez GUI)
    │   ├── TripFilter.java        kryteria + matches(Trip)
    │   └── TripSort.java          enum: sposoby sortowania (każdy z Comparatorem)
    ├── gui/                       WYGLĄD
    │   ├── MainFrame.java         okno, CardLayout, wczytanie danych, okienka błędów
    │   ├── WelcomePanel.java      ekran powitalny z grafiką
    │   ├── SearchPanel.java       wyszukiwarka: pasek, filtry, lista wyników
    │   ├── FilterPanel.java       kolumna z filtrami
    │   ├── TripCard.java          jedna karta wyniku
    │   └── Theme.java             kolory i czcionki w jednym miejscu
    ├── i18n/                      TŁUMACZENIA
    │   ├── Messages.java          get(key) / format(key, args...)
    │   ├── messages.properties    teksty po polsku (domyślne)
    │   └── messages_en.properties teksty po angielsku
    └── img/
        └── welcome.jpg            tło ekranu powitalnego
```

Podział na pakiety oddziela **dane** (`model`), **dostęp do pliku** (`data`), **logikę**
(`filters`) i **wygląd** (`gui`). Na przykład `TripFilter` nie wie nic o Swingu, a `TripRepository`
nie wie nic o oknach.

---

## Co się dzieje po uruchomieniu

```
main()
 └─ SwingUtilities.invokeLater(...)          ← everything below runs on the EDT
     ├─ useNimbus()                          ← look and feel BEFORE any component exists
     └─ new MainFrame()
         ├─ Messages: loads messages.properties (first use of the class)
         ├─ new WelcomePanel(onStart)        ← card "welcome"
         ├─ loadTrips()
         │   ├─ TripRepository.load()        ← reads data/trips.txt
         │   ├─ bad lines  → "Uwaga" dialog, the rest is loaded
         │   └─ no file    → "Błąd" dialog, Trip.sampleTrips() used instead
         ├─ new SearchPanel(trips, onBack)   ← card "search"
         └─ size 1280×720, min 800×500, centred
     .setVisible(true)
```

1. **`TravelAgency.main`** tworzy okno wewnątrz `SwingUtilities.invokeLater`, czyli na wątku
   EDT (Event Dispatch Thread). To jedyny wątek, który może bezpiecznie zmieniać Swinga.
2. **Nimbus** ustawiany jest przed utworzeniem okna, bo komponent pobiera wygląd w chwili tworzenia.
3. **`MainFrame`** używa `CardLayout`. Oba ekrany są „kartami” w jednym oknie i przełącza się
   między nimi przez `showCard(...)`.
4. Panele nie znają `MainFrame`. Dostają **callback** (`Runnable`) i wywołują go np. po kliknięciu
   przycisku: `new WelcomePanel(() -> showCard(SEARCH))`.

---

## Ekrany

### Ekran powitalny (`WelcomePanel`)
- Tło: `img/welcome.jpg` wczytane z classpath (`getResource`), rysowane w `paintComponent`,
  przeskalowane do okna i lekko przyciemnione (`Theme.OVERLAY`).
- Jeśli obrazka brak, panel rysuje jednolite tło `Theme.PRIMARY`, a program działa dalej.
- Tytuł, podtytuł i przycisk „Szukaj wycieczki” są wyśrodkowane przez `GridBagLayout`.

### Wyszukiwarka (`SearchPanel`, układ `BorderLayout`)

| Miejsce | Zawartość |
|---|---|
| `NORTH` | przycisk „Powrót”, pole „Szukaj”, licznik „Znaleziono: N” |
| `WEST` | `FilterPanel` w `JScrollPane` |
| `CENTER` | lista `TripCard` (`BoxLayout.Y_AXIS`) w `JScrollPane`, tylko przewijanie pionowe |

**Filtry (`FilterPanel`)**, każdy innym typem kontrolki:

| Filtr | Kontrolka | Uwagi |
|---|---|---|
| Cena maksymalna | `JSlider` | zakres 0 do najwyższej ceny zaokrąglonej w górę do 1000; etykieta aktualizowana przez `ChangeListener` |
| Kierunek | `JComboBox<String>` | „Wszystkie” + kraje wzięte z danych (`distinct().sorted()`) |
| Wyżywienie | `JCheckBox` × 5 | z `Board.values()`; nic nie zaznaczone oznacza dowolne |
| Dojazd | `JRadioButton` w `ButtonGroup` | „Wszystkie” + `Transport.values()` |
| Minimalna ocena | `JSpinner` | `SpinnerNumberModel(0.0, 0.0, 5.0, 0.5)` |
| Sortowanie | `JComboBox<TripSort>` | cena rosnąco/malejąco, ocena rosnąco/malejąco |

**Kiedy wyniki się odświeżają:**
- tekst w polu „Szukaj” od razu przy każdej zmianie (`DocumentListener`),
- pozostałe filtry po kliknięciu **„Zastosuj”**,
- **„Resetuj”** przywraca domyślne wartości i odświeża listę,
- **„Powrót”** czyści pole i filtry (`resetSearch()`), a potem wraca na ekran powitalny.

**Jak powstaje lista wyników (`SearchPanel.refreshResults`):**

```
FilterPanel.buildFilter(text)  →  TripFilter (all criteria in one object)
allTrips.stream()
    .filter(filter::matches)                      ← TripFilter decides
    .sorted(sortOrder.getComparator())            ← TripSort decides
    .toList()
→ removeAll() → new TripCard(...) for each → revalidate() + repaint()
```

`TripFilter.matches` sprawdza kolejno: nazwa zawiera tekst (bez rozróżniania wielkości liter),
cena ≤ maks., kierunek, wyżywienie, dojazd, ocena ≥ min. Pierwszy niespełniony warunek zwraca
`false`. `null` albo pusty zbiór oznacza „bez ograniczenia”.

**Karta wyniku (`TripCard`):**
- po lewej: nazwa, kraj i daty (od–do, liczba dni), wyżywienie · dojazd, udogodnienia,
- po prawej: cena, gwiazdki `★★★★☆ 4.3` i liczba wolnych miejsc. Gdy miejsc jest
  ≤ `FEW_PLACES` (5), napis jest czerwony i pogrubiony.

---

## Plik z danymi: `data/trips.txt`

Jedna wycieczka w jednej linii, pola rozdzielone `;`, udogodnienia rozdzielone `,`.
Linie puste i zaczynające się od `#` są pomijane.

```
# name;destination;price;startDate;days;amenities;rating;transport;board;freePlaces
Hotel Aurora;Grecja;3499;2027-07-12;7;POOL,BEACH,WIFI;4.5;PLANE;ALL_INCLUSIVE;20
```

| Pole | Format |
|---|---|
| price, rating | liczba z **kropką** (`3499.5`), czytana przez `Double.parseDouble` |
| startDate | ISO `rrrr-mm-dd`, czytana przez `LocalDate.parse` |
| transport, board, amenities | **nazwy stałych enuma** (`PLANE`, `ALL_INCLUSIVE`, `POOL`), czytane przez `valueOf` |

Plik jest czytany i zapisywany w **UTF-8**. `TripRepository.save()` zapisuje dane w tym samym
formacie; będzie używane po rezerwacjach.

---

## Obsługa błędów

| Sytuacja | Gdzie obsłużona | Efekt |
|---|---|---|
| Brak pliku / brak dostępu (`IOException`) | `MainFrame.loadTrips` | okienko **Błąd** z pełną ścieżką; start z `Trip.sampleTrips()` |
| Zła linia w pliku | `TripRepository.parseLine` zamienia wyjątek na `TripParseException` z numerem linii; `load()` zbiera błędy | okienko **Uwaga** z listą „Linia N: przyczyna”; pozostałe wycieczki się wczytują |
| Złe wartości w wycieczce (cena < 0, ocena poza 0–5, dni ≤ 0, miejsca < 0, pusta nazwa) | konstruktor `Trip` (`IllegalArgumentException`) | traktowane jak zła linia |
| Brak obrazka powitalnego | `WelcomePanel.loadImage` | jednolite tło zamiast zdjęcia |
| Brak klucza tłumaczenia lub pliku `.properties` | `Messages.get` | zamiast tekstu widać `!klucz!`; program się nie wywraca |

Wyjątki łapane w `parseLine` (multi-catch):
- `IllegalArgumentException`: obejmuje `NumberFormatException`, `Enum.valueOf`, złą liczbę pól
  i walidację `Trip`,
- `DateTimeParseException`, która nie dziedziczy po `IllegalArgumentException`.

`load()` zwraca `record LoadResult(List<Trip> trips, List<TripParseException> errors)`.

---

## Tłumaczenia (i18n)

- Żaden tekst widoczny dla użytkownika nie jest wpisany w kod. GUI woła
  `Messages.get("klucz")` albo `Messages.format("klucz", wartości...)`.
- Teksty są w `i18n/messages.properties` (PL, domyślny) i `messages_en.properties` (EN).
- Enumy wyświetlają się po polsku, bo ich `toString()` zwraca `Messages.get("board." + name())`.
  Do pliku trafia `name()` (`ALL_INCLUSIVE`), więc plik z danymi nie zależy od języka.
- Dodanie języka to nowy plik `messages_xx.properties` z tymi samymi kluczami.

---

## Wygląd

- `Theme` zbiera wszystkie kolory i czcionki. Zmiana koloru przewodniego to jedna linia.
- Look and Feel Nimbus, z powrotem do domyślnego wyglądu, jeśli Nimbus jest niedostępny.
- Szare tło listy i białe karty z cienką ramką.
- Minimalny rozmiar okna 800×500. Okno startuje na środku ekranu.

---

## Realizacja wymagań

| Wymaganie | Gdzie |
|---|---|
| **Na 3** | |
| Okno powitalne z grafiką i przyciskiem, przejście do osobnego panelu | `WelcomePanel`, `MainFrame` (`CardLayout`) |
| Wycieczka: kierunek, cena, termin, udogodnienia, ocena, dojazd, wyżywienie | `Trip` + enumy |
| Pole wyszukiwania po nazwie | `SearchPanel` (`JTextField` + `DocumentListener`) |
| Różne opcje filtrowania | `FilterPanel`: suwak, lista, checkboxy, radiobuttony, spinner |
| Wyniki jeden pod drugim, przewijane | `SearchPanel` (`BoxLayout` + `JScrollPane`), `TripCard` |
| Korekta filtrów obok wyników i aktualizacja | `FilterPanel` (WEST) + „Zastosuj” / „Resetuj” |
| **Na 4** | |
| Każda klasa w osobnym pliku | 16 plików `.java` w 5 pakietach |
| Współpraca z plikiem `.txt` | `TripRepository` + `data/trips.txt` |
| Obsługa błędów | `TripParseException`, okienka w `MainFrame`, walidacja w `Trip` |
| **Na 5** | |
| Okno tworzone w `invokeLater()` w `main` | `TravelAgency.main` ✅ |
| ≥ 2 własne wątki, promocja z `Thread.sleep`, `synchronized`, kończenie wątków, `join`/`Timer` | 🚧 w przygotowaniu (etap 8: `PromoBanner`, symulacja rezerwacji) |
