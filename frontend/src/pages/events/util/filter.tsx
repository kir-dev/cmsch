import { GROUP_BY_DAY_OPTIONS, stringifyTimeStamp } from '../../../util/core-functions.util'
import { EventListView } from '../../../util/views/event.view'

export enum FILTER {
  ALL = 'all',
  CATEGORY = 'category',
  PLACE = 'place',
  DAY = 'day'
}

export const mapper = (f: FILTER, e: EventListView) => {
  switch (f) {
    case FILTER.ALL:
      throw 'Cannot map if filter is set to all'
    case FILTER.DAY:
      return stringifyTimeStamp(e.timestampStart, GROUP_BY_DAY_OPTIONS)
    default:
      return e[f]
  }
}
