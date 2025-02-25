package collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Map;
import org.example.collection.SimpleArrayList;
import org.example.collection.SimpleList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SimpleArrayListTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessCases {

        @DisplayName("리스트에 데이터를 추가하면 마지막에 추가된다.")
        @Test
        public void addReturnBoolean() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";

            // when
            final boolean actual = myList.add(value);

            // then
            assertThat(myList.size()).isEqualTo(1);
            assertThat(actual).isTrue();
        }

        @DisplayName("인덱스에 해당하는 위치에 값을 추가한다.")
        @Test
        public void addIndex() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";
            final int index = 0;
            myList.add("temp value");

            // when
            myList.add(index, value);

            // then
            assertThat(myList.get(index)).isEqualTo(value);
        }

        @DisplayName("특정 위치의 값을 수정하고, 이전 값을 반환한다.")
        @Test
        public void set() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";
            final String target = "target";
            myList.add(value);

            // when
            final String prevValue = myList.set(0, target);

            // then
            assertThat(prevValue).isEqualTo(value);
            assertThat(myList.get(0)).isEqualTo(target);
        }

        @DisplayName("특정 위치의 값을 가져온다.")
        @Test
        public void get() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value1 = "value";
            final String value2 = "target";
            myList.add(value1);
            myList.add(value2);

            // when
            final String data1 = myList.get(0);
            final String data2 = myList.get(1);

            // then
            assertThat(value1).isEqualTo(data1);
            assertThat(value2).isEqualTo(data2);
        }

        @DisplayName("리스트에 값이 존재하는지 여부를 검사한다.")
        @Test
        public void contains() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";
            final String target = "target";
            myList.add(value);

            // when
            final boolean actual1 = myList.contains(value);
            final boolean actual2 = myList.contains(target);

            // then
            assertThat(actual1).isTrue();
            assertThat(actual2).isFalse();
        }

        @DisplayName("값의 위치를 가져온다.")
        @Test
        public void indexOf() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";
            final String target = "target";
            myList.add(value);

            // when
            final int actual1 = myList.indexOf(value);
            final int actual2 = myList.indexOf(target);

            // then
            assertThat(actual1).isEqualTo(0);
            assertThat(actual2).isEqualTo(-1);
        }

        @DisplayName("현재 리스트의 사이즈를 반환한다.")
        @Test
        public void size() throws Exception {
            // given
            final SimpleList myList = new SimpleArrayList();
            final String value = "value";
            myList.add(value);

            // when
            final int actual = myList.size();

            // then
            assertThat(actual).isEqualTo(1);
        }

        @Test
        public void arrayList() {
            final SimpleList values = new SimpleArrayList();
            values.add("first");
            values.add("second");


            // TODO values에 담긴 모든 값을 출력한다.
            System.out.println(values);

            assertThat(values.add("x")).isTrue(); // 세 번째 값을 추가한다.
            assertThat(values.size()).isEqualTo(3); // list의 크기를 구한다.
            assertThat(values.get(0)).isEqualTo("first"); // 첫 번째 값을 찾는다.
            assertThat(values.contains("first")).isTrue(); // "first" 값이 포함되어 있는지를 확인한다.
            assertThat(values.remove(0)).isEqualTo("first"); // 첫 번째 값을 삭제한다.
            assertThat(values.size()).isEqualTo(2); // 값이 삭제 됐는지 확인한다.

        }

    }

    @Nested
    @DisplayName("실패 테스트")
    class FailCases {
    }
}
