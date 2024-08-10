import { generateUniqueCodes } from './codes.js'
import { padNumberToSameLength } from '../util.js'

const getTokenName = (name, index) => name.replace('{index}', index)

const generateTokenData = (i, token, args) => {
  const {
    count,
    name,
    visible,
    type,
    icon,
    rarity,
    score,
    action,
    activeTarget,
    displayIconUrl,
    displayDescription,
    availableFrom,
    availableUntil
  } = args

  return {
    title: getTokenName(name, padNumberToSameLength(i + 1, count)),
    index: i,
    token,
    visible,
    type,
    icon,
    rarity,
    score,
    action,
    activeTarget,
    displayIconUrl,
    displayDescription,
    availableFrom,
    availableUntil
  }
}

export const generateTokens = (args) => {
  const codes = generateUniqueCodes({ count: args.count, prefix: args.tokenPrefix, length: args.tokenLength })
  return Array.from({ length: args.count }).map((_, i) => generateTokenData(i, codes[i], args))
}
