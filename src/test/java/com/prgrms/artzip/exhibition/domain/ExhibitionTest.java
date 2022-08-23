package com.prgrms.artzip.exhibition.domain;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.prgrms.artzip.common.ErrorCode.*;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.GYEONGGI;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.SEOUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Exhibition 엔티티 테스트")
class ExhibitionTest {

    @Nested
    @DisplayName("name 필드 테스트")
    class NameValidationTest {

        @Test
        @DisplayName("name이 null인 경우 테스트")
        void testNameNull() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_NAME.getMessage());
        }

        @Test
        @DisplayName("name의 길이가 너무 긴 경우 테스트")
        void testNameTooLong() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_NAME.getMessage());
        }
    }

    @Nested
    @DisplayName("period 필드 테스트")
    class PeriodValidationTest {

        @Test
        @DisplayName("startDate null인 경우 테스트")
        void testStartDateNull() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("김이름")
                    .endDate(LocalDate.now())
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_PERIOD.getMessage());
        }

        @Test
        @DisplayName("endDate null인 경우 테스트")
        void testEndDateNull() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("김이름")
                    .startDate(LocalDate.now())
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_PERIOD.getMessage());
        }

        @Test
        @DisplayName("endDate가 startDate보다 이른 경우")
        void testStartDateIsAfter() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("김이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().minusDays(2))
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_PERIOD.getMessage());
        }
    }

    @Nested
    @DisplayName("description 필드 테스트")
    class DescriptionValidationTest {

        @Test
        @DisplayName("description 너무 긴 경우 테스트")
        void testDescriptionTooLong() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description(
                            "The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC \"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
                                    + "The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC \"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
                                    + "The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC \"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
                                    + "The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC \"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
                                    + "The standard Lorem Ipsum passage, used since the 1500s Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\" Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC \"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
                    .latitude(123.321)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_DESCRIPTION.getMessage());
        }
    }

    @Nested
    @DisplayName("location 필드 테스트")
    class LocationValidationTest {

        @Test
        @DisplayName("잘못된 위도 테스트")
        void testWrongLatitude() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(-100.0)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_COORDINATE.getMessage());
        }

        @Test
        @DisplayName("잘못된 경도 테스트")
        void testWrongLongitude() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(-199.9)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHBN_COORDINATE.getMessage());
        }

        @Test
        @DisplayName("area null인 경우 테스트")
        void testAreaNull() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(129.9)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_AREA.getMessage());
        }

        @Test
        @DisplayName("place가 너무 긴경우 테스트")
        void testWrongPlace() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(129.9)
                    .area(SEOUL)
                    .place(
                            "전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_PLACE.getMessage());
        }

        @Test
        @DisplayName("address가 너무 긴경우 테스트")
        void testWrongAddress() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(129.9)
                    .area(SEOUL)
                    .place("전시")
                    .address(
                            "경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 경기도 용인시 수지구 ")
                    .inquiry("010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_ADDRESS.getMessage());
        }
    }

    @Nested
    @DisplayName("inquiry 필드 테스트")
    class InquiryValidationTest {

        @Test
        @DisplayName("inquiry가 너무 긴 경우 테스트")
        void testWrongInquiry() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry(
                            "010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000 010-0000-0000")
                    .fee("1,000원")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_INQUIRY.getMessage());
        }
    }

    @Nested
    @DisplayName("fee 필드 테스트")
    class FeeValidationTest {

        @Test
        @DisplayName("feer가 너무 긴 경우 테스트")
        void testWrongFee() {
            assertThatThrownBy(() -> Exhibition.builder()
                    .seq(12345)
                    .name("이름")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .description("설명 입니다.")
                    .latitude(35.1)
                    .longitude(123.123)
                    .area(GYEONGGI)
                    .place("전시관")
                    .address("경기도 용인시 수지구")
                    .inquiry("010-0000-0000")
                    .fee(
                            "1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 1,000원 ")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
                    .build()
            )
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_FEE.getMessage());
        }
    }

    @DisplayName("url 검증 테스트")
    @ParameterizedTest
    @MethodSource("thumbnailParameter")
    void testUrlValidation(String url) {
        Exhibition exhibition = Exhibition.builder()
                .seq(12345)
                .name("전시회 제목")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .latitude(35.321)
                .longitude(123.123)
                .area(GYEONGGI)
                .place("전시관")
                .address("경기도 용인시 수지구")
                .inquiry("010-0000-0000")
                .fee("1,000원")
                .thumbnail(url)
                .build();

        assertThat(exhibition.getThumbnail()).isEqualTo(url);
    }

    private static Stream<Arguments> thumbnailParameter() {
        return Stream.of(
                Arguments.of("https://www.example-thumbnail-image.png"),
                Arguments.of("https://www.example-thumbnail-image.jpg"),
                Arguments.of("https://www.example-thumbnail-image.jpeg"),
                Arguments.of("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg"),
                Arguments.of("http://www.culture.go.kr/upload/rdf/22/01/show_2022011310122915168.png"),
                Arguments.of("http://soma.kspo.or.kr"),
                Arguments.of("https://www.hangeul.go.kr/traceHangeul/traceHangeul1List.do?curr_menu_cd=0103010100"),
                Arguments.of("https://www.hangeul.go.kr/traceHangeul/traceHangeul1List.do?curr_menu_cd=0103010100"),
                Arguments.of("http://galleryraon.com/?page_id=2472#upcoming")
        );
    }
}