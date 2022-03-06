# SimpleCalendarStructure

Use "@" as keywords
Use $ to link to keyword

## MainActivity

Default will open a $list, and user can click the button below screen to switch to $calendar or $profile.
If open with other app or widget, it can transfer $openEvent and do something quickly.


## @list

A fragment can show a list what you can write something, like remark or memo.
It have lot of group with group name and a lot of check box below the group.

Structure like:
```text
    group
    - item
    - item
    group
    - item
    - item
    - item
    group
    - item
```


## @calendar

A fragment can show calendar that user can write something with date and time.
It can send notification to notify user.

If the day is today, it will have a circle background, and if the day is selected, it also will have a ring around the date.
When user choose a date, below the calendar will show the list of $calendarPlan

## @profile

A fragment can show the user profile like list create, list complete etc.
Also it can open $settings screen to set something custom like.


## Widget

This app will contain 2 home-screen widget:

- $ListWidget
- $CalendarWidget


## @ListWidget

This widget will contain some item with group.
What item need to show can custom it.

## @CalendarWidget

This widget will contain normal calendar and a $calendarPlan witch soon
User can click the date and open $calendar and switch to that day to see $calendarPlan

## @openEvent

selectCalendar - use time(milli-second) as parameter and will auto change screen to $calendar and auto choose the date
addCalendarPlan - use to open $calendarPlan editor with parameter `time` as time use to calendar and `content` as text to store
addListItem - use to open $listItem editor with parameter `group` as group name and `content` as text to store

## Other Keywords

@calendarPlan - something need to do of that date