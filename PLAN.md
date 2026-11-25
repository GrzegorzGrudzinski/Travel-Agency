# Plan pracy: Biuro Podróży (Java Swing)

Kolejność jest ułożona tak, żeby po każdym etapie program **kompilował się i działał**.
Po każdym etapie: uruchom, sprawdź, zrób commit (`git add -A && git commit -m "Etap X: ..."`).

Docelowa struktura plików:

```
BiuroPodrozy/
├── data/
│   └── trips.txt              ← dane wycieczek (etap 6)
├── resources/
│   └── welcome.jpg            ← grafika na ekran powitalny (etap 2)
└── src/
    ├── TravelAgency.java      ← main
    ├── model/
    │   ├── Trip.java
    │   ├── Board.java         ← enum: wyżywienie
    │   └── Transport.java     ← enum: dojazd
    ├── data/
    │   └── TripRepository.java
    ├── search/
    │   └── TripFilter.java
    ├── gui/
    │   ├── MainFrame.java
    │   ├── WelcomePanel.java
    │   ├── SearchPanel.java
    │   ├── FilterPanel.java
    │   ├── TripCard.java
    │   └── PromoBanner.java
    └── threads/
        ├── PromoCountdown.java
        └── BookingSimulator.java
```

---

## Etap 0: Porządki w obecnym kodzie (15 min)

- [ ] `.gitignore`: zmień `*.class,` na `*.class` (bez przecinka, bo inaczej wpis nie działa).
- [ ] `MainFrame`: zmień `extends JWindow` na `extends JFrame` i konfiguruj `this`
      zamiast tworzyć w środku osobny `new JFrame(...)`.
- [ ] Tytuł okna: `"Biuro Podróży"` zamiast `"Test"`.
- [ ] Popraw karty: po starcie ma się pokazać **ekran powitalny**, a napisy na przyciskach
      mają pasować do tego, dokąd faktycznie prowadzą.
- [ ] `main` w `TravelAgency` (to wymóg na 5, warto od razu):

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
}
```

✅ Test: okno się otwiera, przyciski przełączają karty w dobrą stronę.

---

## Etap 1: Model: klasa `Trip` (1 h)

- [ ] `model/Board.java`, enum wyżywienia: `BRAK, SNIADANIA, HB, FB, ALL_INCLUSIVE`
      (warto dodać pole z ładną nazwą do wyświetlania, np. `"All inclusive"`).
- [ ] `model/Transport.java`, enum dojazdu: `SAMOLOT, AUTOKAR, WLASNY, POCIAG`.
- [ ] `model/Trip.java` z polami (wymagane przez prowadzącego):
  - `String name` (nazwa hotelu lub wycieczki, przeszukiwana z klawiatury)
  - `String destination` (kierunek / kraj)
  - `double price`
  - `LocalDate startDate`, `int days` (termin)
  - `List<String> amenities` (udogodnienia: basen, wifi, plaża…)
  - `double rating` (ocena, np. 1.0–5.0)
  - `Transport transport`
  - `Board board`
  - `int freeSeats` (przyda się w etapie 8, dodaj już teraz)
- [ ] Konstruktor, gettery, `toString()`.
- [ ] Tymczasowo: metoda `static List<Trip> sampleTrips()` zwracająca **10–12 RÓŻNORODNYCH**
      wycieczek (różne kraje, ceny od ~800 do ~9000 zł, różne terminy, wszystkie rodzaje
      wyżywienia i dojazdu, oceny od słabych do świetnych). Różnorodność jest ważniejsza od liczby,
      bo ma pokazać, że filtry działają.

✅ Test: w `main` wypisz `sampleTrips()` w konsoli.

---

## Etap 2: Szkielet GUI: okno + ekran powitalny (1–2 h)

- [ ] `MainFrame`: `CardLayout` z dwiema kartami, `WELCOME` → `WelcomePanel` i `SEARCH` → `SearchPanel`
      (na razie pusty `JPanel` z napisem).
- [ ] `WelcomePanel extends JPanel`:
  - grafika: najprościej `JLabel` z `ImageIcon`, a ładniej nadpisane
    `paintComponent(Graphics g)` i rysowanie obrazka na całe tło,
  - duży tytuł / slogan,
  - przycisk **„Szukaj wycieczki”**, który przełącza na kartę `SEARCH`.
- [ ] Przekazanie akcji przycisku: najprościej konstruktor `WelcomePanel(Runnable onStart)`,
      a w `MainFrame`: `new WelcomePanel(() -> cardLayout.show(cards, SEARCH))`.
- [ ] Wczytanie grafiki: jeśli plik nie istnieje, panel ma się wyświetlić bez obrazka
      (`ImageIcon` po prostu będzie pusty; sprawdź `icon.getIconWidth() <= 0`).

✅ Test: start → ekran powitalny z grafiką → klik → panel wyszukiwarki.

---

## Etap 3: Prosta wyszukiwarka z jednym parametrem (2 h)

- [ ] `TripCard extends JPanel`: jeden wynik (nazwa, kierunek, termin, cena, ocena w gwiazdkach,
      wyżywienie, dojazd, udogodnienia). Ramka/obramowanie, stała wysokość
      (`setMaximumSize(new Dimension(Integer.MAX_VALUE, 130))`).
- [ ] `SearchPanel extends JPanel` z `BorderLayout`:
  - `NORTH`: pole `JTextField` „Szukaj po nazwie…”
  - `CENTER`: `JScrollPane` z panelem wyników w `BoxLayout.Y_AXIS` (wyniki jeden pod drugim)
- [ ] Metoda `refreshResults()`: `resultsPanel.removeAll()` → dodaj `TripCard` dla pasujących →
      `resultsPanel.revalidate(); resultsPanel.repaint();`
- [ ] Wyszukiwanie: `name.toLowerCase().contains(query.toLowerCase())`.
- [ ] Odświeżanie na bieżąco: `DocumentListener` na polu tekstowym (albo na start: Enter / przycisk).
- [ ] Gdy brak wyników, pokaż napis „Brak wycieczek spełniających kryteria”.
- [ ] Przewijanie kółkiem jest wolne, więc ustaw `scrollPane.getVerticalScrollBar().setUnitIncrement(16)`.

✅ Test: wpisanie fragmentu nazwy zawęża listę, a wyczyszczenie pola pokazuje wszystko.

---

## Etap 4: Rozbudowa wyszukiwarki: RÓŻNE filtry (2–3 h)

- [ ] `search/TripFilter.java`: klasa przechowująca kryteria + metoda `boolean matches(Trip t)`.
      Dzięki temu logika filtrowania jest oddzielona od GUI (łatwo to pokazać i zmienić na obronie).
- [ ] `FilterPanel` (np. `WEST` w `SearchPanel`, pionowo) z **różnymi typami** kontrolek:
  - `JSlider`: maksymalna cena (z etykietą pokazującą aktualną wartość, `ChangeListener`)
  - `JComboBox<String>`: kierunek (pozycja „Wszystkie” + kraje wzięte z listy wycieczek)
  - `JCheckBox`-y: wyżywienie (można zaznaczyć kilka)
  - `JRadioButton` w `ButtonGroup` lub `JComboBox`: dojazd
  - `JSpinner`: minimalna ocena
  - opcjonalnie: data od / do, liczba dni, wymagane udogodnienie
  - `JComboBox` sortowania: cena rosnąco / malejąco / ocena
- [ ] Przyciski **„Zastosuj filtry”** i **„Resetuj”** (wymaganie: korekta filtrów obok wyników
      i aktualizacja wyników).
- [ ] `refreshResults()` buduje `TripFilter` z aktualnych wartości kontrolek → `stream().filter(...)`.
- [ ] Liczba wyników nad listą: „Znaleziono: 5 wycieczek”.

✅ Test: przetestuj każdy filtr osobno i kilka naraz. Dobierz dane tak, żeby każdy filtr
coś realnie odfiltrowywał.

---

## Etap 5: Layout i estetyka (1–2 h)

- [ ] Spójna paleta kolorów (stałe w jednej klasie, np. `gui/Theme.java`) i czcionki.
- [ ] Marginesy: `BorderFactory.createEmptyBorder(...)`, odstępy `Box.createVerticalStrut(...)`.
- [ ] `TripCard`: miniaturka zdjęcia (opcjonalnie), wyróżniona cena, gwiazdki `★★★★☆`.
- [ ] Przycisk „← Powrót” z wyszukiwarki do ekranu powitalnego.
- [ ] Opcjonalnie: Look and Feel `Nimbus` (`UIManager.setLookAndFeel(...)` przed utworzeniem okna).
- [ ] `setMinimumSize` okna, żeby layout się nie rozsypał przy zmniejszaniu.

---

## Etap 6: Plik tekstowy `.txt` (1–2 h), wymóg na 4

- [ ] Ustal format, jedna wycieczka w jednej linii, pola oddzielone `;`, udogodnienia `,`:

```
# nazwa;kierunek;cena;data;dni;udogodnienia;ocena;dojazd;wyzywienie;miejsca
Hotel Aurora;Grecja;3499.00;2026-07-12;7;basen,wifi,plaża;4.5;SAMOLOT;ALL_INCLUSIVE;20
Zakopane Chill;Polska;899.00;2026-02-01;4;sauna,wifi;4.0;WLASNY;SNIADANIA;12
```

- [ ] `data/TripRepository.java`:
  - `List<Trip> load(Path file)`: `Files.readAllLines` albo `BufferedReader` w try-with-resources,
    pomija puste linie i linie zaczynające się od `#`, parsuje każdą linię do `Trip`.
  - `void save(Path file, List<Trip> trips)`: zapis w tym samym formacie
    (np. po rezerwacji, żeby zapisać zmienioną liczbę miejsc).
- [ ] Przenieś dane z `sampleTrips()` do `data/trips.txt` i usuń zahardkodowaną listę
      (albo zostaw ją jako awaryjne dane, patrz etap 7).
- [ ] Uwaga na ścieżkę: względna ścieżka `data/trips.txt` liczy się od katalogu, z którego uruchamiasz
      program. Uruchamiaj z katalogu `BiuroPodrozy/`.
- [ ] Kodowanie: czytaj i zapisuj jako `StandardCharsets.UTF_8` (polskie znaki!).

✅ Test: zmień cenę w pliku → uruchom → nowa cena w programie.

---

## Etap 7: Obsługa błędów (1 h), wymóg na 4

- [ ] Brak pliku / brak uprawnień: `catch (IOException e)` → `JOptionPane.showMessageDialog(...)`
      z czytelnym komunikatem + start z pustą listą lub danymi awaryjnymi.
- [ ] Uszkodzona linia (zła liczba pól, `NumberFormatException`, `DateTimeParseException`,
      `IllegalArgumentException` z `Enum.valueOf`): **pomiń tylko tę linię**, policz błędne
      i wypisz ostrzeżenie „Pominięto 2 błędne wpisy (linie 5, 9)”.
- [ ] Opcjonalnie: własny wyjątek `TripParseException extends Exception` z numerem linii
      (dobrze wygląda na obronie).
- [ ] Brak grafiki na ekranie powitalnym: program działa dalej.

✅ Test: zmień nazwę pliku → komunikat zamiast crasha. Wpisz w pliku `abc` jako cenę →
ta linia pominięta, reszta działa.

---

## Etap 8: Wątki (3–4 h), wymóg na 5

Zrób dwa niezależne wątki + porządne zamykanie. Każdy punkt poniżej to osobny wymóg ze `wymagania.md`.

### 8a. Wątek 1: promocja ograniczona czasowo
- [ ] `gui/PromoBanner extends JPanel`: pasek u góry wyszukiwarki,
      np. „🔥 -20% na Grecję! Zostało 00:30”.
- [ ] `threads/PromoCountdown implements Runnable`:

```java
public class PromoCountdown implements Runnable {
    private volatile boolean running = true;   // flaga zakończenia
    private final PromoBanner banner;
    private final int seconds;

    public void run() {
        try {
            for (int s = seconds; s >= 0 && running; s--) {
                final int left = s;
                SwingUtilities.invokeLater(() -> banner.setTimeLeft(left)); // GUI tylko przez EDT!
                Thread.sleep(1000);
            }
            SwingUtilities.invokeLater(banner::showExpired);  // baner znika / zmienia się
        } catch (InterruptedException e) {
            // przerwano: po prostu wychodzimy z run()
        }
    }
    public void stop() { running = false; }
}
```

- [ ] Uruchomienie: `promoThread = new Thread(new PromoCountdown(...), "promo"); promoThread.start();`
- [ ] Opcjonalnie: w trakcie promocji ceny wybranego kierunku są obniżone w wynikach,
      a po końcu wracają do normalnych.

### 8b. Wątek 2+: symulacja klientów rezerwujących miejsca (`synchronized`)
- [ ] W `Trip` metoda rezerwacji zabezpieczona `synchronized`:

```java
public synchronized boolean reserve(int count) {
    if (freeSeats >= count) {
        try { Thread.sleep(5); } catch (InterruptedException e) {  // sztuczne opóźnienie,
            Thread.currentThread().interrupt(); return false;       // żeby wyścig był widoczny
        }
        freeSeats -= count;
        return true;
    }
    return false;
}
public synchronized int getFreeSeats() { return freeSeats; }
```

- [ ] `threads/BookingSimulator`: uruchamia np. 5 wątków „klientów”, każdy kilka razy próbuje
      zarezerwować 1–3 miejsca na tę samą wycieczkę (losowo, z `Thread.sleep` między próbami).
- [ ] **`join()`** (wymóg „kontrola wątków”): wątek koordynatora czeka na wszystkich klientów
      (`for (Thread t : clients) t.join();`), potem przez `invokeLater` pokazuje podsumowanie:
      „Zarezerwowano 20/20, pozostało 0 miejsc”.
- [ ] Przycisk w GUI „Symuluj rezerwacje” + wyświetlanie liczby wolnych miejsc w `TripCard`.
- [ ] Logi w konsoli: `"[klient-3] zarezerwował 2, zostało 7"` (ułatwia pokazanie na obronie).
- [ ] **Przygotuj demo bez `synchronized`**: usuń słowo, uruchom, liczba miejsc
      spada poniżej zera. To trzeba umieć pokazać i wyjaśnić.

### 8c. Opcjonalnie: `javax.swing.Timer`
- [ ] Np. zegar w rogu okna lub migający napis promocji. Kod Timera wykonuje się na **EDT**,
      więc można w nim bezpośrednio zmieniać GUI (dobre pytanie z obrony).

### 8d. Poprawne zamykanie: po zamknięciu okna NIE MOŻE działać żaden wątek
- [ ] W `MainFrame`: `setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)` + `WindowListener`:

```java
addWindowListener(new WindowAdapter() {
    @Override public void windowClosing(WindowEvent e) {
        promo.stop();            // flaga
        promoThread.interrupt(); // przerywa sleep()
        bookingSimulator.stopAll(); // interrupt() wszystkich klientów
        // opcjonalnie: join() z timeoutem, żeby poczekać na zakończenie
        dispose();
    }
});
```

- [ ] **Nie używaj** `Thread.stop()` ani `System.exit()` jako „zamykacza”. Najlepszy dowód
      poprawności: przy `dispose()` JVM sama się kończy, gdy żaden wątek nie działa.
- [ ] Nie ustawiaj `setDaemon(true)` jako obejścia, bo prowadzący może to uznać za oszustwo.

✅ Test: zamknij okno w trakcie odliczania i w trakcie symulacji. Proces w terminalu
musi się zakończyć od razu (sprawdź też `jps`, że nie wisi).

---

## Etap 9: Sprawdzenie końcowe

- [ ] Przejdź po `wymagania.md` punkt po punkcie i odhacz.
- [ ] Każda klasa w osobnym pliku, sensowne nazwy, brak martwego kodu (np. pusta `WelcomePanel` z etapu 0).
- [ ] Uruchom z czystego katalogu: `javac -d out $(find src -name '*.java') && java -cp out TravelAgency`.
- [ ] Wszystkie zmiany GUI z innych wątków idą przez `invokeLater` (przeszukaj kod: `grep -n "set\|add" src/threads/`).

---

## Etap 10: Przygotowanie do obrony

Umieć pokazać w kodzie: **każdy wątek** (gdzie tworzony, gdzie `start()`, jak się kończy),
**dane współdzielone** (`freeSeats`), **zabezpieczenie** (`synchronized`) i **co się stanie bez niego**.

Krótkie odpowiedzi na pytania z listy:

| Pytanie | Odpowiedź w skrócie |
|---|---|
| Proces a wątek | Proces ma własną pamięć; wątki jednego procesu dzielą pamięć (sterta), każdy ma własny stos. |
| `start()` a `run()` | `start()` tworzy nowy wątek i w nim woła `run()`; samo `run()` to zwykłe wywołanie metody w bieżącym wątku. |
| Stany wątku | `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`. |
| `sleep()` / `join()` | `sleep(ms)`: usypia bieżący wątek (TIMED_WAITING), **nie zwalnia** monitora. `t.join()`: czekaj, aż wątek `t` się skończy. |
| Wyścig danych | Kilka wątków jednocześnie czyta/zapisuje te same dane, wynik zależy od przeplotu. `synchronized` sprawia, że tylko jeden wątek naraz wykonuje sekcję na danym monitorze (+ widoczność zmian). |
| Deadlock / starvation / livelock | Deadlock: wątki czekają na siebie nawzajem w nieskończoność. Starvation: wątek nigdy nie dostaje zasobu/CPU. Livelock: wątki działają, ale ciągle ustępują sobie i nie robią postępu. |
| Priorytet | `setPriority()` to tylko wskazówka dla planisty systemu, **nie gwarantuje** kolejności. |
| `wait()` / `notify()` | Wątek w bloku `synchronized` czeka na warunek (`wait()` zwalnia monitor); inny wątek go budzi `notify()`/`notifyAll()`. |
| EDT i `invokeLater()` | Event Dispatch Thread to jedyny wątek, który może bezpiecznie zmieniać Swinga. `invokeLater()` wrzuca zadanie do kolejki EDT. |
| Na jakim wątku działa `javax.swing.Timer` | Na **EDT**, więc w jego akcji nie wolno robić długich operacji, a GUI można zmieniać bezpośrednio. |

Przećwicz **zmiany na żywo**, które prowadzący może zlecić:
- zmienić czas promocji / tekst banera,
- dodać nowy filtr (np. minimalna liczba dni), z klasą `TripFilter` to zmiana w 2 miejscach,
- zmienić liczbę klientów / miejsc w symulacji,
- usunąć `synchronized` i pokazać efekt,
- dodać nowe pole do wycieczki i do pliku `.txt`.
