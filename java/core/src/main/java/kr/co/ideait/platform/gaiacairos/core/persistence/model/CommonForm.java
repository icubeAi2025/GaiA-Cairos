package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class CommonForm {

    String searchType;
    String searchText;
    String wildcardText;

    // int page = 0; // page number is indexed from 0
    // int size = 5;

    /* tui grid에서 가져오는 값 */
    int page = 1; // page number is indexed from 0s
    int perPage = 10;
    String sortColumn = null;
    boolean sortAscending; // true : asc , false: desc

    List<String> sort; // sort=col1,asc&sort=col2,desc

    // like map
    Map<String, Object> like;

    // public Pageable getPageable() {
    // if (sort != null) {
    // List<Order> orders = sort.stream().map(s -> {
    // String[] sortArr = s.split(",");
    // switch (sortArr.length) {
    // case 1:
    // return Order.asc(sortArr[0]);
    // case 2:
    // return Direction.fromString(sortArr[1]) == Direction.ASC ?
    // Order.asc(sortArr[0])
    // : Order.desc(sortArr[0]);
    // default:
    // return Order.asc(sortArr[0]);
    // }
    // }).collect(Collectors.toList());
    // Sort requestedSort = Sort.by(orders);
    // return PageRequest.of(page, size, requestedSort);
    // }
    // return PageRequest.of(page, size);
    // }

    String superProjectNo;
    String superContractNo;

    public Pageable getPageable() {
        if (sortColumn != null) {
            String sortColumnSnakeCase = convertCamelToSnake(sortColumn);
            if (sortAscending) {
                return PageRequest.of(page - 1, perPage, Sort.by(sortColumnSnakeCase).ascending());
            } else {
                return PageRequest.of(page - 1, perPage, Sort.by(sortColumnSnakeCase).descending());
            }
        }
        return PageRequest.of(page - 1, perPage);
    }

    public void setLikeBySearchText(String... members) {
        String searchText = getSearchText();
        if (searchText != null) {
            List.of(members).forEach(member -> {
                try {
                    Field field = this.getClass().getDeclaredField(member);
                    field.setAccessible(true);
                    field.set(this, "%" + searchText + "%");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("sql like(add '%' to string') set error", e);
                }
            });
        }
    }

    public void createLike(String... members) {
        String searchText = getSearchText();
        if (searchText != null) {
            if (getLike() == null) {
                setLike(new HashMap<>());
            }
            like = getLike();
            List.of(members).forEach(member -> {
                like.put(member, "%" + searchText + "%");
            });
        }
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        this.wildcardText = "%" + searchText + "%";
    }

    public MybatisInput toMybatisInput() {
        MybatisInput input = new ObjectMapper().convertValue(this, MybatisInput.class);
        input.add("user", UserAuth.get());
        return input;
    }

    public MybatisInput toMybatisInput(UserAuth user) {
        return new ObjectMapper().convertValue(this, MybatisInput.class).add("user", user);
    }

    // 카멜케이스를 스네이크 케이스로 변환하는 메서드
    private String convertCamelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder(camelCase.length() + 10);
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    // 슈퍼관리자가 프로젝트와 계약번호를 파라미터로 변경할 수 있도록합니다.
    public void adminSuperChange() {
        UserAuth.get(true).superChange(superProjectNo, superContractNo);
    }

}
