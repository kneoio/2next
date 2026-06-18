package com.semantyca.mixpla.model.cnst;

/**
 * Role of a scene in the agenda.
 * LOOP     – the continuous baseline; fills all time not claimed by a one-time scene.
 * ONE_TIME – a scheduled event that preempts the loop at its start time, then hands back.
 */
public enum SceneType {
    LOOP,
    ONE_TIME
}
