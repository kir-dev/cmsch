import { endOfDay, isSameDay } from 'date-fns'
import { EventListView } from '../../../../util/views/event.view'
import { EventBoxItem } from './EventBox'

export function calculatePosition(minTimestamp: number, maxTimestamp: number, timestamp: number) {
  return ((timestamp - minTimestamp) / (maxTimestamp - minTimestamp)) * 100
}

export function mapEventsForDay(events: EventListView[], day: Date): EventBoxItem[] {
  const endDate = endOfDay(day)
  const formattedEventListForDay = events
    .sort((a, b) => (a.timestampStart === b.timestampStart ? a.timestampEnd - b.timestampEnd : a.timestampStart - b.timestampStart))
    .map<EventListView>((e) => ({ ...e, timestampStart: e.timestampStart * 1000, timestampEnd: e.timestampEnd * 1000 }))
    .filter((e) => isSameDay(e.timestampStart, day) || isSameDay(e.timestampEnd + 1000, day))

  return formattedEventListForDay.map<EventBoxItem>((e, index) => ({
    ...e,
    conflictingEventsBefore: countConflictingEventsBefore(formattedEventListForDay, index),
    top: Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampStart), 100)),
    bottom: 100 - Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampEnd), 100))
  }))
}

function countConflictingEventsBefore(events: EventListView[], index: number) {
  const event = events[index]
  let count = 0
  for (let i = index - 1; i >= 0; i--) {
    if (events[i].timestampEnd > event.timestampStart && events[i].timestampStart <= event.timestampStart) {
      count++
    } else {
      break
    }
  }
  return count
}
