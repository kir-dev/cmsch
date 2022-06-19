import { Organizer } from '../../../api/contexts/config/types'

export const parseOrganizerArrayJSON = (array: string | undefined): Organizer[] => JSON.parse(array || '[]')
