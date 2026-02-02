package com.revworkforce.model;

import java.sql.Timestamp;

/**
 * Represents a company announcement.
 * Maps to the 'announcements' table.
 */
public class Announcement {

    private int announcementId;
    private String title;
    private String content;
    private String postedBy;
    private Timestamp postedDate;

    public int getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public Timestamp getPostedDate() { return postedDate; }
    public void setPostedDate(Timestamp postedDate) { this.postedDate = postedDate; }
}
