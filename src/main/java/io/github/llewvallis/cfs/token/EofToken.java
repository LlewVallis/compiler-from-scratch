package io.github.llewvallis.cfs.token;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This is a "fake" token that represents the end of the input. An EOF token is produced
 * indefinitely when no more "real" tokens exist.
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public final class EofToken extends Token {}
