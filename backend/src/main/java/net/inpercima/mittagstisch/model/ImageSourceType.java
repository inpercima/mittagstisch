package net.inpercima.mittagstisch.model;

/**
 * Describes whether an image sent to the AI has already been cropped to the
 * relevant days or is a full-page image (e.g. a rendered PDF page).
 */
public enum ImageSourceType {
    /** The image was pre-cropped to show only today and tomorrow. */
    CROPPED,
    /** The image is a full-page view (e.g. a rendered PDF page). */
    FULL_PAGE
}
