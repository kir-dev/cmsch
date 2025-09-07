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

  const eventLayouts = calculateEventLayout(formattedEventListForDay)

  return formattedEventListForDay.map<EventBoxItem>((e, index) => ({
    ...e,
    top: Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampStart), 100)),
    bottom: 100 - Math.max(0, Math.min(calculatePosition(day.getTime(), endDate.getTime(), e.timestampEnd), 100)),
    width: eventLayouts[index].width,
    left: eventLayouts[index].left
  }))
}

function calculateEventLayout(events: EventListView[]) {
  const eventLayouts = events.map(() => ({ width: 100, left: 0 }))
  if (events.length === 0) {
    return eventLayouts
  }

  const conflicts: number[][] = []
  for (let i = 0; i < events.length; i++) {
    conflicts[i] = []
    for (let j = 0; j < events.length; j++) {
      if (i !== j && events[i].timestampStart < events[j].timestampEnd && events[i].timestampEnd > events[j].timestampStart) {
        conflicts[i].push(j)
      }
    }
  }

  const groups: number[][] = []
  const visited = new Array(events.length).fill(false)
  for (let i = 0; i < events.length; i++) {
    if (!visited[i]) {
      const group: number[] = []
      const queue = [i]
      visited[i] = true
      while (queue.length > 0) {
        const u = queue.shift()!
        group.push(u)
        for (const v of conflicts[u]) {
          if (!visited[v]) {
            visited[v] = true
            queue.push(v)
          }
        }
      }
      groups.push(group)
    }
  }

  for (const group of groups) {
    const columns: number[][] = []
    for (const eventIndex of group) {
      let placed = false
      for (let i = 0; i < columns.length; i++) {
        if (columns[i].every((placedIndex) => !conflicts[eventIndex].includes(placedIndex))) {
          columns[i].push(eventIndex)
          placed = true
          break
        }
      }
      if (!placed) {
        columns.push([eventIndex])
      }
    }

    const numColumns = columns.length
    for (let i = 0; i < numColumns; i++) {
      for (const eventIndex of columns[i]) {
        eventLayouts[eventIndex].width = 100 / numColumns
        eventLayouts[eventIndex].left = (100 / numColumns) * i
      }
    }
  }

  return eventLayouts
}
