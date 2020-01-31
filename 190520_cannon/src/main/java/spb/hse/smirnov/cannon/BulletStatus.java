package spb.hse.smirnov.cannon;

/** Presents possible statuses of bullet */
public enum BulletStatus {
    /** Bullet has not reached a border */
    ALIVE,
    /** Bullet has reached a border but still should be seen */
    ZOMBIE,
    /** Bullet should not be seen */
    DEAD
}
