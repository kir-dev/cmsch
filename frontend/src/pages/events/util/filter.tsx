import { GROUP_BY_DAY_OPTIONS, stringifyTimeStamp } from '../../../util/core-functions.util'
import type { EventListView } from '../../../util/views/event.view'

export const Filter = {
  ALL: 'all',
  CATEGORY: 'category',
  PLACE: 'place',
  DAY: 'day'
}
export type Filter = (typeof Filter)[keyof typeof Filter]

export const mapper = (f: Filter, e: EventListView) => {
  switch (f) {
    case Filter.ALL:
      throw 'Cannot map if filter is set to all'
    case Filter.DAY:
      return stringifyTimeStamp(e.timestampStart, GROUP_BY_DAY_OPTIONS)
    default:
      console.assert(Object.keys(e).includes(f))
      return e[f as keyof EventListView].toString()
  }
}
