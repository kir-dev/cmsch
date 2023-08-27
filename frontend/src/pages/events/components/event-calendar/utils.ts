import { endOfDay, isSameDay } from 'date-fns'
import { EventListView } from '../../../../util/views/event.view'
import { EventBoxItem } from './EventBox'

export function calculatePosition(minTimestamp: number, maxTimestamp: number, timestamp: number) {
  return Math.round(((timestamp - minTimestamp) / (maxTimestamp - minTimestamp)) * 100)
}

export function mapEventsForDay(events: EventListView[], day: Date): EventBoxItem[] {
  const endDate = endOfDay(day)
  return events
    .map<EventListView>((e) => ({ ...e, timestampStart: e.timestampStart * 1000, timestampEnd: e.timestampEnd * 1000 }))
    .filter((e) => isSameDay(e.timestampStart, day) || isSameDay(e.timestampEnd + 1000, day))
    .sort((a, b) => (a.timestampStart === b.timestampStart ? a.timestampEnd - b.timestampEnd : a.timestampStart - b.timestampStart))
    .map<EventBoxItem>((e) => ({
      ...e,
      top: Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampStart), 100)),
      bottom: 100 - Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampEnd), 100))
    }))
}
