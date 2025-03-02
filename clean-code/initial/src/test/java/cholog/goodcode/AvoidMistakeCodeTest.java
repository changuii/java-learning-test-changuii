package cholog.goodcode;

import java.util.stream.Collectors;
import javax.swing.text.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 좋은 코드의 기준은 사람마다 다르지만 대부분의 사람들이 동의하는 몇 가지 기준이 있습니다. 그 기준 중 유지보수성과 확장성은 매우 중요한 기준입니다. 실수를 피하는 코드는 유지보수성을 높이고 버그를 줄이는데
 * 도움을 줍니다. 유지보수성과 확장성을 위한 실수를 방지하는 코드를 작성하는 방법을 알아봅니다.
 */
public class AvoidMistakeCodeTest {
    /**
     * 아래 코드는 최대 5까지만 움직이는 자동차를 구현한 코드입니다. 자동차와 위치를 포장하여 응집도를 높이고 유지보수성 및 확장성을 고려한 코드입니다. 아래 코드는 원시값을 포장했지만 같은 위치 객체를
     * 사용하며 의도와 다르게 동작하는 코드입니다. 어떻게 같은 위치 객체를 사용할 때 발생할 수 있는 실수를 방지할 수 있을까?
     */
    @Test
    @DisplayName("어떻게 같은 위치 객체를 사용할 때 발생할 수 있는 실수를 방지할 수 있을까?")
    void 어떻게_같은_위치_객체를_사용할_때_발생할_수_있는_실수를_방지할_수_있을까() {
        // TODO: 같은 위치 객체를 사용할 때 발생할 수 있는 실수를 방지할 수 있는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:24: Position 객체를 불변으로 만들고, Car에서 position을 변경할 수 있게 만든다.
        class Position {
            private int value;

            Position() {
                this(0);
            }

            Position(final int value) {
                this.value = value;
            }

            public Position increase() {
                return new Position(value + 1);
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                final Position position = (Position) o;
                return value == position.value;
            }

            @Override
            public int hashCode() {
                return Objects.hash(value);
            }
        }

        class Car {

            private final String name;
            private Position position;

            Car(final String name, final Position position) {
                this.name = name;
                this.position = position;
            }

            // TODO 3/2/25 09:34: 불변 객체로 만들어, 활용한다. 이를 통해 객체를 공유하더라도 문제가 발생하지 않는다.
            public void forward() {
                position = position.increase();
            }

            public Position position() {
                return position;
            }
        }

        final var position = new Position();

        final var neoCar = new Car("네오", position);
        final var brownCar = new Car("브라운", position);

        neoCar.forward();

        // Note: 네오의 자동차만 움직였기 때문에 브라운의 자동차는 움직이지 않아야 한다.
        assertThat(neoCar.position()).isEqualTo(new Position(1));
        assertThat(brownCar.position()).isEqualTo(new Position(0));
    }

    /**
     * 원시값을 불변 객체로 만들어 실수를 방지하는 방법입니다. 불변 객체는 객체의 상태를 변경할 수 없기 때문에 객체의 상태를 변경하는 실수를 방지할 수 있습니다. 불변 객체는 변경이 있을 때 마다 새로운
     * 객체를 생성하기 때문에 성능상의 이슈가 발생할 수 있습니다. 불변 객체를 사용할 때 성능상의 이슈를 해결하는 방법은 무엇일까?
     * <p>
     * 참고: <a href="https://en.wikipedia.org/wiki/Immutable_object">불변 객체</a>
     */
    @Test
    @DisplayName("불변 객체를 사용할 때 성능상의 이슈를 해결하는 방법은 무엇일까?")
    void 불변_객체를_사용할_때_성능상의_이슈를_해결하는_방법은_무엇일까() {
        // TODO: 불변 객체를 사용할 때 성능상의 이슈를 해결하는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:24: 자주 사용하는 불변 객체들을 캐싱하여 동일한 객체를 재사용한다. 불변 객체는 상태가 변하지 않기 때문에 위험하지 않다.
        record Position(int value) {

            // TODO 3/2/25 10:15: 캐시를 통해서, 성능상 이슈를 해결한다.
            private static final Map<Integer, Position> CACHE = new ConcurrentHashMap<>();

            private static Position startPoint(){
                return of(0);
            }

            private static Position of(final int value){
                return CACHE.computeIfAbsent(value, Position::new);
            }

            public Position increase() {
                return new Position(value + 1);
            }
        }

        record Car(
                String name,
                Position position
        ) {
            public Car forward() {
                return new Car(name, position.increase());
            }
        }

        final var position = Position.startPoint();

        var neoCar = new Car("네오", position);
        final var brownCar = new Car("브라운", position);

        // Note: Car 객체가 불변 객체가 되면서 위치가 이동될 때 마다 새로운 객체가 생성된다.
        neoCar = neoCar.forward();

        assertThat(neoCar.position()).isEqualTo(new Position(1));
        assertThat(brownCar.position()).isEqualTo(new Position(0));
    }

    /**
     * 정적 팩터리 메서드를 만들고 내부에서 캐싱하여 성능상의 이슈를 해결하는 방법입니다. 객체를 재활용할 경우 매번 새로운 객체가 생성되지 않기 때문에 성능상의 이슈를 해결할 수 있습니다. 하지만 지금의
     * 방법은 캐싱되는 객체가 많을수록 메모리 사용량이 증가할 수 있습니다. 메모리 사용량을 최소화하는 방법은 무엇일까?
     */
    @Test
    @DisplayName("메모리 사용량을 최소화하는 방법은 무엇일까?")
    void 메모리_사용량을_최소화하는_방법은_무엇일까() {
        // TODO: 메모리 사용량을 최소화하는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:24: 자주 사용하는 범위를 캐싱하여 사용한다. ex) Integer 의 캐싱
        record Position(int value) {
            private static final Map<Integer, Position> CACHE = new ConcurrentHashMap<>();

            public static Position startingPoint() {
                return valueOf(0);
            }

            public static Position valueOf(final int value) {
                return CACHE.computeIfAbsent(value, Position::new);
            }

            public Position increase() {
                return valueOf(value + 1);
            }
        }

        record Car(
                String name,
                Position position
        ) {
            private static final Map<String, Car> CACHE = new ConcurrentHashMap<>();

            public static Car of(final String name, final Position position) {
                return CACHE.computeIfAbsent(toKey(name, position), key -> new Car(key, position));
            }

            private static String toKey(final String name, final Position position) {
                return name + position.value();
            }

            public Car forward() {
                // Note: 움직일 때 마다 캐싱된 객체가 재활용된다. 하지만 캐싱된 객체가 많을수록 메모리 사용량이 증가한다.
                return Car.of(name, position.increase());
            }
        }

        final var position = Position.startingPoint();

        var neoCar = Car.of("네오", position);
        var brownCar = Car.of("브라운", position);

        neoCar = neoCar.forward();

        assertThat(neoCar.position()).isEqualTo(Position.valueOf(1));
        assertThat(brownCar.position()).isEqualTo(Position.valueOf(0));
    }

    /**
     * 자주 사용될 객체만 캐싱하는 방법입니다. 자주 사용되는 객체만 캐싱할 경우 메모리 사용량을 최소화할 수 있습니다. 어떤 객체를 캐싱할지 계산하는 것도 비용이 들고, 객체 그래프가 복잡할 경우 캐싱하는
     * 것도 복잡해질 수 있습니다. 또한 JVM의 경우 GC의 성능이 우리가 생각하는 것 보다 훨씬 더 좋기 때문에 캐싱을 하지 않는 것이 더 좋을 수도 있습니다. 객체 그래프가 깊을 때 문제를 해결하는 방법은
     * 무엇일까?
     */
    @Test
    @DisplayName("객체 그래프가 깊을 때 문제를 해결하는 방법은 무엇일까?")
    void 객체_그래프가_깊을_때_문제를_해결하는_방법은_무엇일까() {
        // TODO: 객체 그래프가 깊을 때 문제를 해결하는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:23: 생각으로는, 굳이 Car는 불변 객체일 필요가 없다고 생각해 캐싱하지 않아도 될 것 같다.
        record PositionForEnhancedCache(int value) {
            private static final int CACHE_MIN = 0;
            private static final int CACHE_MAX = 5;
            private static final Map<Integer, PositionForEnhancedCache> CACHE = IntStream.range(CACHE_MIN, CACHE_MAX)
                    .boxed()
                    .collect(toMap(identity(), PositionForEnhancedCache::new));

            public static PositionForEnhancedCache startingPoint() {
                return valueOf(0);
            }

            public static PositionForEnhancedCache valueOf(final int value) {
                // Note: 자주 사용되는 객체만 캐싱하여 메모리 사용량을 최소화한다.
                // TODO 3/2/25 10:16: Integer 객체처럼, 자주 사용하는 범위에 대해서만 캐싱을 처리하고, 나머지 부분에 대해서는 새로 만들어 반환한다.
                if (CACHE_MIN <= value && value <= CACHE_MAX) {
                    return CACHE.get(value);
                }
                return new PositionForEnhancedCache(value);
            }

            public PositionForEnhancedCache increase() {
                return valueOf(value + 1);
            }
        }

        record CarForEnhancedCache(
                String name,
                PositionForEnhancedCache position
        ) {
            private static final Map<String, CarForEnhancedCache> CACHE = new ConcurrentHashMap<>();

            public static CarForEnhancedCache of(final String name, final PositionForEnhancedCache position) {
                return CACHE.computeIfAbsent(toKey(name, position), key -> new CarForEnhancedCache(key, position));
            }

            private static String toKey(final String name, final PositionForEnhancedCache position) {
                return name + position.value();
            }

            public CarForEnhancedCache forward() {
                return CarForEnhancedCache.of(name, position.increase());
            }
        }

        final var position = PositionForEnhancedCache.startingPoint();

        var neoCar = CarForEnhancedCache.of("네오", position);
        var brownCar = CarForEnhancedCache.of("브라운", position);

        neoCar = neoCar.forward();

        assertThat(neoCar.position()).isEqualTo(PositionForEnhancedCache.valueOf(1));
        assertThat(brownCar.position()).isEqualTo(PositionForEnhancedCache.valueOf(0));
    }

    /**
     * Car 내부에서 Position 객체를 변경하는 방법입니다. 적정한 시점까지만 불변 객체를 사용하면 불변 객체의 장점을 살리면서 성능상의 이슈를 해결할 수 있습니다. 성능적인 이점만 존재하는 것은
     * 아닙니다. 불변 객체의 장점을 살리면서 가변 객체의 장점도 살릴 수 있습니다. 동일한 컨텍스트에서만 불변 객체를 사용하고, 다른 컨텍스트에서 사용될 수 있는 지점에선 가변 객체처럼 사용하게 하는 것이
     * 좋습니다. 하지만 이 기준은 상황과 설계에 따라 달라질 수 있습니다. 구현할 때 가변 객체로도 구현해보고, 불변 객체로도 구현해보면서 어떠한 방법이 더 좋은지 판단해보는 것이 좋습니다.
     */
    @Test
    @DisplayName("적정한 시점까지만 불변 객체를 사용한다.")
    void 적정한_시점까지만_불변_객체를_사용한다() {
        record Position(int value) {
            Position() {
                this(0);
            }

            public Position increase() {
                return new Position(value + 1);
            }
        }

        // TODO 3/2/25 10:17: Car 객체는, 가변 객체로 사용함으로써 적절한 시점까지만, 불변 객체로 활용한다.
        // TODO 3/2/25 10:18: 자동차 객체는, 위치가 변하기 때문에 굳이 불변 객체일 필요가 없다.  (특히, 캐싱작업도 애매하게 된다.)
        class Car {
            private final String name;
            private Position position;

            Car(final String name, final Position position) {
                this.name = name;
                this.position = position;
            }

            public void forward() {
                position = position.increase();
            }

            public Position getPosition() {
                return position;
            }
        }

        final var position = new Position();

        final var neoCar = new Car("네오", position);
        final var brownCar = new Car("브라운", position);

        neoCar.forward();

        assertThat(neoCar.getPosition()).isEqualTo(new Position(1));
        assertThat(brownCar.getPosition()).isEqualTo(new Position());
    }

    /**
     * 아래 코드는 우승한 자동차들을 구하는 코드입니다. 자동차 경주 객체 내부에 자동차 객체들이 존재하고 있고, 외부에서 조작할 수 있는 위험이 존재하고 있습니다. 객체의 상태를 변경할 수 있는 위험은 실수로
     * 인한 버그를 발생시킬 수 있습니다. 객체의 상태를 변경할 수 있는 위험을 방지하는 방법은 무엇일까?
     */
    @Test
    @DisplayName("객체의 상태를 변경할 수 있는 위험을 방지하는 방법은 무엇일까?")
    void 객체의_상태를_변경할_수_있는_위험을_방지하는_방법은_무엇일까() {
        // TODO: 객체의 상태를 변경할 수 있는 위험을 방지하는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:22: 지금 생각으로는, Car 객체가 외부로 노출되었을 경우, 위험을 방지하기 위해 가변 객체를 생성자 복사 후 반환하는 것이다.
        record Position(int value) {
            Position() {
                this(0);
            }

            public Position increase() {
                return new Position(value + 1);
            }
        }

        class Car {
            private final String name;
            private Position position;

            Car(final String name, final Position position) {
                this.name = name;
                this.position = position;
            }

            public void forward() {
                position = position.increase();
            }

            public boolean matchPosition(final Position position) {
                return this.position.equals(position);
            }

            public Position getPosition() {
                return position;
            }

            @Override
            public String toString() {
                return "Car{" +
                        "name='" + name + '\'' +
                        ", position=" + position +
                        '}';
            }
        }

        class RacingGame {
            private final List<Car> participants;

            RacingGame(final List<Car> participants) {
                // TODO 3/2/25 09:30: 아하,, 이 부분을 말하는 것 같다. 외부의 컬렉션 조작에 의해 RacingGame의 상태가 변경될 수 있다.
                this.participants = new ArrayList<>(participants);
            }

            public List<Car> selectWinners() {
                return matchCarsByPosition(calculateWinnerPosition());
            }

            private Position calculateWinnerPosition() {
                return participants.stream()
                        .map(Car::getPosition)
                        .max(Comparator.comparingInt(Position::value))
                        .orElseThrow(() -> new IllegalStateException("참가자가 없습니다."));
            }

            private List<Car> matchCarsByPosition(final Position position) {
                return participants.stream()
                        .filter(car -> car.matchPosition(position))
                        .toList();
            }

            List<Car> getParticipants() {
                return participants.stream()
                        // TODO 3/2/25 09:30: 가변 객체를 복사하여 반환한다.,, ? 가변 객체도 깊은 복사 해야하나?
                        .collect(Collectors.toList());
            }
        }

        final var neoCar = new Car("네오", new Position());
        final var brownCar = new Car("브라운", new Position(1));
        final var participants = new ArrayList<>(List.of(neoCar, brownCar));
        final var racingGame = new RacingGame(participants);

        final var winners = racingGame.selectWinners();
        assertThat(winners).containsExactly(brownCar);

        // Note: 외부에서 조작할 수 있는 위험이 존재하고 있다.
        participants.add(new Car("브리", new Position(2)));
        racingGame.getParticipants().add(new Car("솔라", new Position(3)));
        assertThat(winners).containsExactly(brownCar);
        assertThat(racingGame.getParticipants()).containsExactlyElementsOf(List.of(neoCar, brownCar));
    }


    /**
     * 방어적 복사를 사용하여 객체의 상태를 변경할 수 있는 위험을 방지하는 방법입니다. 하지만 방어적 복사를 사용할 경우 객체의 상태를 변경할 수 있는 위험을 방지할 수 있지만 성능상의 이슈가 발생할 수
     * 있습니다. 방어적 복사를 사용할 때 성능상의 이슈를 해결하는 방법은 무엇일까?
     */
    @Test
    @DisplayName("방어적 복사를 사용할 때 성능상의 이슈를 해결하는 방법은 무엇일까?")
    void 방어적_복사를_사용할_때_성능상의_이슈를_해결하는_방법은_무엇일까() {
        // TODO: 방어적 복사를 사용할 때 성능상의 이슈를 해결하는 방법을 고민 후 개선해보세요.
        // TODO 3/2/25 09:25: 어떻게 이슈를 해결할 수 있을까?
        record Position(int value) {
            Position() {
                this(0);
            }

            public Position increase() {
                return new Position(value + 1);
            }
        }

        class Car {
            private final String name;
            private Position position;

            Car(final String name, final Position position) {
                this.name = name;
                this.position = position;
            }

            public void forward() {
                position = position.increase();
            }

            public boolean matchPosition(final Position position) {
                return this.position.equals(position);
            }

            public Position getPosition() {
                return position;
            }

            @Override
            public String toString() {
                return "Car{" +
                        "name='" + name + '\'' +
                        ", position=" + position +
                        '}';
            }
        }

        class RacingGame {
            private final List<Car> participants;

            RacingGame(final List<Car> participants) {
                this.participants = new ArrayList<>(participants);
            }

            public List<Car> selectWinners() {
                return matchCarsByPosition(calculateWinnerPosition());
            }

            private Position calculateWinnerPosition() {
                return participants.stream()
                        .map(Car::getPosition)
                        .max(Comparator.comparingInt(Position::value))
                        .orElseThrow(() -> new IllegalStateException("참가자가 없습니다."));
            }

            private List<Car> matchCarsByPosition(final Position position) {
                return participants.stream()
                        .filter(car -> car.matchPosition(position))
                        .toList();
            }

            List<Car> getParticipants() {
                // Note: 매번 새로운 리스트를 생성하여 성능상의 이슈가 발생할 수 있다.
                return new ArrayList<>(participants);
            }
        }

        final var neoCar = new Car("네오", new Position());
        final var brownCar = new Car("브라운", new Position(1));
        final var participants = new ArrayList<>(List.of(neoCar, brownCar));
        final var racingGame = new RacingGame(participants);

        final var winners = racingGame.selectWinners();
        assertThat(winners).containsExactly(brownCar);

        participants.add(new Car("브리", new Position(2)));
        racingGame.getParticipants().add(new Car("솔라", new Position(3)));
        assertThat(winners).containsExactly(brownCar);
        assertThat(racingGame.getParticipants()).containsExactlyElementsOf(List.of(neoCar, brownCar));
    }

    /**
     * 불변 컬렉션을 사용하여 객체의 상태를 변경할 수 있는 위험을 방지하는 방법입니다. 응답하는 컬렉션을 불변 컬렉션으로 만들어 객체의 상태를 변경할 수 있는 위험을 방지할 수 있습니다. 입력을 받는 컬렉션을
     * 불변 컬렉션을 만드는 것은 그대로 외부에서 조작할 수 있는 위험이 존재합니다. 따라서 입력받는 컬렉션은 방어적 복사로, 응답하는 컬렉션은 불변 컬렉션으로 만드는 것이 좋습니다.
     */
    @Test
    @DisplayName("입력을 받는 컬렉션은 방어적 복사로, 응답하는 컬렉션은 불변 컬렉션으로 만드는 것이 좋다.")
    void 입력을_받는_컬렉션은_방어적_복사로_응답하는_컬렉션은_불변_컬렉션으로_만드는_것이_좋다() {
        record Position(int value) {
            Position() {
                this(0);
            }

            public Position increase() {
                return new Position(value + 1);
            }
        }

        class Car {
            private final String name;
            private Position position;

            Car(final String name, final Position position) {
                this.name = name;
                this.position = position;
            }

            public void forward() {
                position = position.increase();
            }

            public boolean matchPosition(final Position position) {
                return this.position.equals(position);
            }

            public Position getPosition() {
                return position;
            }

            @Override
            public String toString() {
                return "Car{" +
                        "name='" + name + '\'' +
                        ", position=" + position +
                        '}';
            }
        }

        class RacingGame {
            private final List<Car> participants;

            RacingGame(final List<Car> participants) {
                // TODO 3/2/25 09:28: 입력 받는 부분은 방어적 복사를 통해 외부의 컬렉션 조작을 방지한다.
                this.participants = new ArrayList<>(participants);
            }

            public List<Car> selectWinners() {
                return matchCarsByPosition(calculateWinnerPosition());
            }

            private Position calculateWinnerPosition() {
                return participants.stream()
                        .map(Car::getPosition)
                        .max(Comparator.comparingInt(Position::value))
                        .orElseThrow(() -> new IllegalStateException("참가자가 없습니다."));
            }

            private List<Car> matchCarsByPosition(final Position position) {
                return participants.stream()
                        .filter(car -> car.matchPosition(position))
                        .toList();
            }

            List<Car> getParticipants() {
                // TODO 3/2/25 09:29: 굳이? 외부에서의 컬렉션 변경을 방지한다.
                // TODO 3/2/25 09:29: 순서가 중요하다면, 외부에서의 컬렉션 변경을 방지하고 아니라면 굳이 불변 컬렉션으로 만들 필요는 없을 것 같다.
                return unmodifiableList(participants);
            }
        }

        final var neoCar = new Car("네오", new Position());
        final var brownCar = new Car("브라운", new Position(1));
        final var participants = new ArrayList<>(List.of(neoCar, brownCar));
        final var racingGame = new RacingGame(participants);

        final var winners = racingGame.selectWinners();
        assertThat(winners).containsExactly(brownCar);

        participants.add(new Car("브리", new Position(2)));
        assertThat(winners).containsExactly(brownCar);
        assertThat(racingGame.getParticipants()).containsExactlyElementsOf(List.of(neoCar, brownCar));

        // Note: 불변 컬렉션을 사용하여 객체의 상태를 변경할 수 있는 위험을 방지할 수 있다.
        assertThatThrownBy(() -> {
            racingGame.getParticipants().add(new Car("솔라", new Position(3)));
        }).isInstanceOf(UnsupportedOperationException.class);
    }
}
