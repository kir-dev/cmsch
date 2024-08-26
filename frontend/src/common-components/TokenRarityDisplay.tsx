import { Box, Flex, Text } from '@chakra-ui/react'

type TokenRarityChipProps = {
  count: string
  max?: number
  color: string
  background: string
}

// based on https://fortnite.fandom.com/wiki/Rarity
const TokenRarityChip = ({ count, max, color, background }: TokenRarityChipProps) => {
  return (
    <Box
      display="inline-block"
      borderRadius="4px 6px 4px 6px"
      transform="skew(-10deg)"
      w="fit-content"
      h="fit-content"
      p="1px 6px"
      lineHeight="normal"
      color={color}
      background={background}
      fontWeight={900}
      fontSize="1.1em"
    >
      <Text as="span" transform="skew(10deg)" fontFamily="Rubik, Impact, var(--chakra-fonts-display), sans-serif">
        {count}
      </Text>
      {max && (
        <Text fontSize=".7em" as="sub">
          /{max}
        </Text>
      )}
    </Box>
  )
}

interface TokenRarityDisplayProps {
  collected: { [rarity: string]: number }
}

export const TokenRarityDisplay = ({ collected }: TokenRarityDisplayProps) => {
  // TODO: get count by rarity from config
  const maxByRarity: { [key: string]: number } = {
    COMMON: 1500,
    UNCOMMON: 500,
    RARE: 250,
    EPIC: 125,
    LEGENDARY: 62
  }

  const rarities = [
    {
      rarity: 'COMMON',
      color: '#b7bfc5',
      background: '#40464d'
    },
    {
      rarity: 'UNCOMMON',
      color: '#61bf00',
      background: '#024f03'
    },
    {
      rarity: 'RARE',
      color: '#00afff',
      background: '#00458a'
    },
    {
      rarity: 'EPIC',
      color: '#ce59ff',
      background: '#4c197b'
    },
    {
      rarity: 'LEGENDARY',
      color: '#ffe8cf',
      background: '#de6e0e'
    }
  ]

  return (
    <Flex gap={1} wrap="wrap">
      {rarities
        .filter((rarity) => collected[rarity.rarity] > 0)
        .map((rarity) => (
          <TokenRarityChip
            key={rarity.rarity}
            count={collected[rarity.rarity].toString()}
            max={maxByRarity[rarity.rarity]}
            color={rarity.color}
            background={rarity.background}
          />
        ))}
      {collected['RAINBOW'] > 0 && (
        <TokenRarityChip
          color="#000"
          count="LGBTQ"
          background={
            'linear-gradient(45deg,' +
            ' #ff0000, #ff8000, #ffff00, #80ff00, #00ff00, #00ff80, #00ffff, #0080ff, #0000ff, #8000ff, #ff00ff, #ff0080' +
            ')'
          }
        />
      )}
    </Flex>
  )
}
