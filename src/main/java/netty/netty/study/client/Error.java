package netty.netty.study.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public enum Error {

    TYPE_A ( 1, "%s connected", true),
    TYPE_B ( 2, "disconnected", false )
    ;

    private final int code;
    private final String message;
    private final boolean repeated;
    @Setter private boolean occurred;
}
