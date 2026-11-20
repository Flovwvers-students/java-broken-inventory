# Отчёт по практике «Сломанный инвентарь»

**ФИО:** Томашук Антон  
**Группа:** 32/2 (М3-02030102-1о2)  
**GitHub:** `@d0sh1kkk`

---

## 1. Результаты первичного запуска (до исправлений)

При первом запуске `InventoryDemo` и `InventoryTests` на исходном коде проявились расхождения с условием:

- `InventoryDemo` дважды вывел `[Attack: Sword, Drink: Potion]`, хотя зелье имело количество `1` и на втором вызове должно было вернуть `Empty: Potion`.
- Из 5 стартовых тестов в `InventoryTests` упали 4:
  - `FAIL duplicate code`: повторное добавление предмета с тем же кодом вернуло `true` вместо `false`.
  - `FAIL search by text`: поиск по новой строке с тем же текстом вернул `null` вместо `Optional[Potion]`.
  - `FAIL missing code`: поиск по отсутствующему коду вернул `null` вместо `Optional.empty`.
  - `FAIL use order and potion quantity`: второй вызов `useAll()` снова вернул `Drink: Potion` вместо `Empty: Potion`.

---

## 2. Найденные ошибки, их причины и исправления

1. **Неизрасходуемое зелье (`src/inventory/Potion.java`):**
   - **Причина:** в методе `use()` при положительном количестве возвращалась строка `"Drink: " + getName()`, но количество зелья не уменьшалось.
   - **Исправление:** перед возвратом `"Drink: " + getName()` добавлено уменьшение количества на единицу через `setQuantity(getQuantity() - 1)`. При `0` метод по-прежнему возвращает `"Empty: " + getName()` и оставляет количество равным `0`.

2. **Хранение в `ArrayList` и разрешение дубликатов (`src/inventory/Inventory.java`):**
   - **Причина:** `Inventory` хранил предметы в `List<Item>` и в `add(Item)` не проверял уникальность кода.
   - **Исправление:** хранилище заменено на `Map<String, Item> items = new LinkedHashMap<>()`. В `add(Item)` добавлена проверка `item == null || items.containsKey(item.getCode())` — в этих случаях метод возвращает `false` без изменения коллекции.

3. **Сравнение строк через `==` и возврат `null` вместо `Optional.empty()` (`src/inventory/Inventory.java`):**
   - **Причина:** в `findByCode(String code)` коды сравнивались по ссылке (`item.getCode() == code`), а при отсутствии совпадения возвращался `null`.
   - **Исправление:** поиск выполняется по ключу в `Map` (`items.get(code)` сравнивает строки через `equals` с учётом регистра). При `code == null` возвращается `Optional.empty()`, иначе — `Optional.ofNullable(items.get(code))`.

4. **Изменяемый список в `listCodes()` (`src/inventory/Inventory.java`):**
   - **Причина:** метод возвращал изменяемый `ArrayList`, который можно было модифицировать извне.
   - **Исправление:** метод возвращает `List.copyOf(items.keySet())` — неизменяемую копию-снимок ключей в порядке добавления.

5. **Удаление по коду (`src/inventory/Inventory.java`):**
   - **Исправление:** в `removeByCode(String code)` добавлена явная защита от `null` и удаление через `items.remove(code) != null`. Остальные элементы сохраняют порядок, а повторно добавленный код попадает в конец `LinkedHashMap`.

6. **Соответствие кодстайлу (`src/inventory/InventoryDemo.java`, `tests/inventory/InventoryTests.java`):**
   - В `InventoryDemo` и `InventoryTests` строковые и числовые тестовые данные вынесены в именованные локальные переменные и константы, а списки `List.of(...)` — в именованные переменные согласно правилам кодстайла курса.

---

## 3. Ответы на теоретические вопросы

### Почему выбрана реализация `LinkedHashMap`
`Map<String, Item>` обеспечивает уникальность кодов и поиск/удаление по коду за $O(1)$ в среднем (с сравнением ключей через `String.equals` с учётом регистра). Среди реализаций `Map`:
- `HashMap` не гарантирует порядок обхода элементов;
- `TreeMap` сортирует ключи по алфавиту, нарушая порядок добавления;
- **`LinkedHashMap`** хранит двусвязный список записей поверх хэш-таблицы, благодаря чему **сохраняет точный порядок добавления**: при удалении элемента порядок остальных не меняется, а при повторном добавлении того же кода он оказывается в конце. Это точно соответствует требованиям для `listCodes()`, `useAll()` и `removeByCode()`.

### Чем пустой `Optional` отличается от `null`
- `null` — отсутствие ссылки на объект; если вызывающий код забудет проверить результат на `null` и вызовет метод (например, `.getName()`), программа упадёт с `NullPointerException`.
- `Optional.empty()` — полноценный объект-контейнер, который **явно** на уровне типа возвращаемого значения (`Optional<Item>`) сигнализирует вызывающему коду, что предмет может отсутствовать, и заставляет безопасно обработать этот случай (`isPresent()`, `orElse(...)`, `ifPresent(...)`), исключая случайный `NullPointerException`.

### Как тест подтверждает, что `listCodes()` возвращает неизменяемый снимок
В тесте `testCodesSnapshot` выполняются две группы проверок:
1. **Неизменяемость:** после получения `List<String> snapshot = inventory.listCodes()` вызывается `snapshot.add(extraCode)` внутри `expectThrows(UnsupportedOperationException.class, ...)`. Выброс `UnsupportedOperationException` доказывает, что список нельзя изменить извне.
2. **Независимость (свойство снимка):** далее в сам `inventory` последовательно добавляется новый предмет (`add`), удаляется существующий (`removeByCode`) и выполняется очистка (`clear`). После каждой операции проверяется, что ранее полученный `snapshot` по-прежнему равен исходному списку `["sword", "healing"]`. Это доказывает, что `listCodes()` возвращает именно копию, а не живое представление (`view`) ключей `Map`.

---

## 4. Сборка и результаты тестов

Команды сборки и запуска (JDK 17+):

```sh
javac --release 17 -encoding UTF-8 -d out -sourcepath src src/inventory/InventoryDemo.java tests/inventory/InventoryTests.java
java -cp out inventory.InventoryDemo
java -cp out inventory.InventoryTests
```

Вывод `InventoryDemo`:

```text
[sword, healing]
[Attack: Sword, Drink: Potion]
[Attack: Sword, Empty: Potion]
```

Вывод `InventoryTests` (все 13 проверок пройдены):

```text
PASS empty inventory
PASS duplicate code
PASS search by text
PASS missing code
PASS use order and potion quantity
PASS null add and remove
PASS same name different codes
PASS case sensitive codes
PASS clear inventory
PASS remove and re-add order
PASS negative quantity
PASS codes snapshot
PASS item validation and base use
```
