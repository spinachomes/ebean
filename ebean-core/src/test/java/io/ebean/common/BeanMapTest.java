package io.ebean.common;

import io.ebean.bean.BeanCollection;
import org.junit.Test;
import org.tests.model.basic.EBasic;

import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


public class BeanMapTest {

  private final EBasic object1 = new EBasic("o1");
  private final EBasic object2 = new EBasic("o2");
  private final EBasic object3 = new EBasic("o3");
  private final EBasic object4 = new EBasic("o4");
  private final EBasic object5 = new EBasic("o5");

  private Map<String, Object> all() {
    Map<String, Object> all = new LinkedHashMap<>();
    all.put("1", object1);
    all.put("2", object2);
    all.put("3", object3);
    return all;
  }

  private Map<String, Object> some() {
    Map<String, Object> all = new LinkedHashMap<>();
    all.put("2", object2);
    all.put("3", object3);
    return all;
  }

  @Test
  public void testAdd() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>();
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);
    map.put("1", object1);
    map.put("4", null);

    assertThat(map.getModifyAdditions()).containsExactly(object1);
    assertThat(map.getModifyRemovals()).isEmpty();

    map.put("1", object1);
    map.put("4", null);
    assertThat(map.getModifyAdditions()).containsExactly(object1);

    map.put("2", object2);
    assertThat(map.getModifyAdditions()).containsExactly(object1, object2);

    map.remove("1");
    assertThat(map.getModifyAdditions()).containsExactly(object2);
    assertThat(map.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testAddAll_given_emptyStart() throws Exception {

    BeanMap<String, Object> set = new BeanMap<>();
    set.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    set.putAll(all());

    assertThat(set.getModifyAdditions()).containsExactly(object1, object2, object3);
    assertThat(set.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testAdd_given_someAlreadyIn() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>(some());
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    assertThat(map.values().contains(object1)).isFalse();
    map.put("1", object1);
    assertThat(map.values().contains(object2)).isTrue();
    map.put("2", object2);

    assertThat(map.getModifyAdditions()).containsExactly(object1);
    assertThat(map.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testAddSome_given_someAlreadyIn() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>(some());
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    map.putAll(all());

    assertThat(map.getModifyAdditions()).containsExactly(object1);
    assertThat(map.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testRemove_given_beansInAdditions() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>();
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);
    map.putAll(all());
    assertThat(map.getModifyAdditions()).containsExactly(object1, object2, object3);

    // act
    map.remove("2");
    map.remove("3");

    assertThat(map.getModifyAdditions()).containsExactly(object1);
    assertThat(map.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testRemoveAll_given_beansInAdditions() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>();
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);
    map.putAll(all());
    assertThat(map.getModifyAdditions()).containsExactly(object1, object2, object3);

    // act
    map.remove("2");
    map.remove("3");

    assertThat(map.getModifyAdditions()).containsExactly(object1);
    assertThat(map.getModifyRemovals()).isEmpty();
  }

  @Test
  public void testRemove_given_beansNotInAdditions() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>(all());
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    map.remove("2");
    map.remove("3");

    // assert
    assertThat(map.getModifyAdditions()).isEmpty();
    assertThat(map.getModifyRemovals()).containsExactly(object2, object3);
  }

  @Test
  public void testRemoveAll_given_beansNotInAdditions() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>(all());
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    map.remove("2");
    map.remove("3");

    // assert
    assertThat(map.getModifyAdditions()).isEmpty();
    assertThat(map.getModifyRemovals()).containsExactly(object2, object3);
  }

  @Test
  public void testClear() throws Exception {

    BeanMap<String, Object> map = new BeanMap<>(all());
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    // act
    map.clear();

    //assert
    assertThat(map.getModifyRemovals()).containsExactly(object1, object2, object3);
    assertThat(map.getModifyAdditions()).isEmpty();
  }

  @Test
  public void testClear_given_someBeansInAdditions() throws Exception {

    BeanMap<String, EBasic> map = newModifyListeningMap();
    map.put("2", object2);
    map.put("3", object3);

    // act
    map.clear();

    //assert
    assertThat(map.getModifyRemovals()).containsExactly(object1);
    assertThat(map.getModifyAdditions()).isEmpty();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void keySet_add_whenModifyListening() {
    BeanMap<String, EBasic> map = newModifyListeningMap();
    map.keySet().add("3");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void keySet_add() {
    BeanMap<String, Object> map = new BeanMap<>();
    map.keySet().add("3");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void keySet_addAll_whenModifyListening() {
    BeanMap<String, EBasic> map = newModifyListeningMap();
    map.keySet().addAll(asList("3", "4"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void keySet_addAll() {
    BeanMap<String, Object> map = new BeanMap<>();
    map.keySet().addAll(asList("3", "4"));
  }

  @Test
  public void keySet_remove() {

    BeanMap<String, Object> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    final Set<String> keySet = map.keySet();
    keySet.remove("1");

    assertThat(keySet.contains("1")).isFalse();
    assertThat(map).doesNotContainKeys("1");
    assertThat(map.get("1")).isNull();

    assertThat(map.getModifyRemovals()).containsExactly(object1);
  }

  @Test
  public void keySet_clear() {

    BeanMap<String, Object> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    final Set<String> keySet = map.keySet();
    keySet.clear();

    assertThat(map).isEmpty();
    assertThat(keySet).isEmpty();

    assertThat(map.getModifyRemovals()).containsExactly(object1, object2);
  }

  @Test
  public void keySet_iterator_remove() {

    BeanMap<String, Object> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.put("3", object3);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    final Set<String> keySet = map.keySet();
    final Iterator<String> iterator = keySet.iterator();
    while (iterator.hasNext()) {
      final String key = iterator.next();
      if (key.equals("2")) {
        iterator.remove();
      }
    }

    assertThat(map).hasSize(2);
    assertThat(keySet).hasSize(2);
    assertThat(keySet).containsExactly("1", "3");
    assertThat(map).containsKeys("1", "3");

    assertThat(map.getModifyRemovals()).containsExactly(object2);
  }

  @Test
  public void keySet_removeAll() {

    BeanMap<String, Object> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.put("3", object3);
    map.put("4", object4);
    map.put("5", object5);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    final Set<String> keySet = map.keySet();
    final boolean changed = keySet.removeAll(asList("2", "3", "5"));

    assertThat(changed).isTrue();
    assertThat(map).hasSize(2);
    assertThat(keySet).hasSize(2);
    assertThat(keySet).containsExactly("1", "4");
    assertThat(map).containsKeys("1", "4");

    assertThat(map.getModifyRemovals()).containsExactly(object2, object3, object5);
  }


  @Test
  public void keySet_retainAll() {

    BeanMap<String, Object> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.put("3", object3);
    map.put("4", object4);
    map.put("5", object5);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);

    final Set<String> keySet = map.keySet();
    final boolean changed = keySet.retainAll(asList("2", "3", "5"));

    assertThat(changed).isTrue();
    assertThat(map).hasSize(3);
    assertThat(keySet).hasSize(3);
    assertThat(keySet).containsExactly("2", "3", "5");
    assertThat(map).containsKeys("2", "3", "5");

    assertThat(map.getModifyRemovals()).containsExactly(object1, object4);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void values_add() {
    BeanMap<String, EBasic> map = new BeanMap<>();
    map.values().add(object3);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void values_addAll() {
    BeanMap<String, EBasic> map = new BeanMap<>();
    map.values().addAll(asList(object3, object5));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void entrySet_add() {
    newModifyListeningMap()
      .entrySet()
      .add(new AbstractMap.SimpleEntry<>("3", object3));
  }

  @Test
  public void entrySet_clear() {
    final BeanMap<String, EBasic> map = newModifyListeningMap();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();
    entries.clear();

    assertThat(entries).isEmpty();
    assertThat(map).isEmpty();
    assertThat(map.getModifyRemovals()).containsExactly(object1);
  }

  @Test
  public void entrySet_remove() {
    final BeanMap<String, EBasic> map = newModifyListeningMap5();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();

    assertThat(map).hasSize(5);

    final boolean existed1 = entries.remove(new AbstractMap.SimpleEntry<>("1", object1));
    assertThat(existed1).isTrue();

    final boolean existed22 = entries.remove(new AbstractMap.SimpleEntry<>("22", object1));
    assertThat(existed22).isFalse();

    assertThat(map).hasSize(4);
    assertThat(map.getModifyRemovals()).containsExactly(object1);
  }

  @Test
  public void entrySet_remove_whenNotEqualValue() {
    final BeanMap<String, EBasic> map = newModifyListeningMap5();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();

    assertThat(map).hasSize(5);

    final boolean modified = entries.remove(new AbstractMap.SimpleEntry<>("1", object2));
    assertThat(modified).isFalse();

    assertThat(map).hasSize(5);
    assertThat(map.getModifyRemovals()).isNull();
  }

  @Test
  public void entrySet_iterator_remove() {
    final BeanMap<String, EBasic> map = newModifyListeningMap5();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();
    final Iterator<Map.Entry<String, EBasic>> iterator = entries.iterator();
    while (iterator.hasNext()) {
      final Map.Entry<String, EBasic> entry = iterator.next();
      if (entry.getKey().equals("2") || entry.getKey().equals("5")) {
        iterator.remove();
      }
    }
    assertThat(map).hasSize(3);
    assertThat(entries).hasSize(3);
    assertThat(map.getModifyRemovals()).containsExactly(object2, object5);
  }

  @Test
  public void entrySet_removeAll() {
    final BeanMap<String, EBasic> map = newModifyListeningMap5();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();

    entries.removeAll(asList(new AbstractMap.SimpleEntry<>("1", object1), new AbstractMap.SimpleEntry<>("3", object4), new AbstractMap.SimpleEntry<>("4", object4)));
    assertThat(map).hasSize(3);
    assertThat(entries).hasSize(3);
    assertThat(map.getModifyRemovals()).containsExactly(object1, object4);
  }

  @Test
  public void entrySet_retainAll() {
    final BeanMap<String, EBasic> map = newModifyListeningMap5();
    final Set<Map.Entry<String, EBasic>> entries = map.entrySet();

    entries.retainAll(asList(new AbstractMap.SimpleEntry<>("1", object1), new AbstractMap.SimpleEntry<>("3", object4), new AbstractMap.SimpleEntry<>("4", object4)));
    assertThat(map).hasSize(2);
    assertThat(entries).hasSize(2);
    assertThat(map.getModifyRemovals()).containsExactly(object2, object3, object5);
  }

  private BeanMap<String, EBasic> newModifyListeningMap() {
    BeanMap<String, EBasic> map = new BeanMap<>();
    map.put("1", object1);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);
    return map;
  }

  private BeanMap<String, EBasic> newModifyListeningMap5() {
    BeanMap<String, EBasic> map = new BeanMap<>();
    map.put("1", object1);
    map.put("2", object2);
    map.put("3", object3);
    map.put("4", object4);
    map.put("5", object5);
    map.setModifyListening(BeanCollection.ModifyListenMode.ALL);
    return map;
  }
}
