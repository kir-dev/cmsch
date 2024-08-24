import { ChevronDownIcon, ChevronUpIcon } from '@chakra-ui/icons'
import { useDisclosure, Link as ChakraLink, Grid, GridItem, Box } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { joinPath, useOpaqueBackground } from '../util/core-functions.util'
import { AbsolutePaths } from '../util/paths'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'

type CollapsableTableRowProps = {
  collapsable: boolean
  data: LeaderBoardItemView & { position: number }
  idx: number
  showGroup: boolean
  suffix?: string
  categorized?: boolean
  showDescription: boolean
}

export const CollapsableTableRow = ({
  collapsable,
  data,
  idx,
  suffix,
  showGroup,
  categorized = false, // order inner items by value
  showDescription
}: CollapsableTableRowProps) => {
  const { isOpen, onToggle } = useDisclosure()
  const bg = idx % 2 === 0 ? useOpaqueBackground(1) : undefined
  const isGroupLink = typeof data.groupId !== 'undefined'

  collapsable = (collapsable && data.items && data.items.length > 0) || false

  let outerColTemplate: string[] = []
  if (!categorized) outerColTemplate.push('[place] auto')
  outerColTemplate.push('[name] 1fr')
  if (showGroup) outerColTemplate.push('[group] 1fr')
  outerColTemplate.push('[score] auto [chevron] 20px')

  let innerColTemplate: string[] = []
  if (categorized) innerColTemplate.push('[place] auto')
  innerColTemplate.push('[name] 1fr [score] auto')
  if (!categorized) innerColTemplate.push('[chevron] 20px')

  return (
    <>
      <Grid
        onClick={() => {
          if (collapsable) onToggle()
        }}
        _hover={{ cursor: collapsable ? 'pointer' : 'default' }}
        fontWeight="bold"
        bg={bg}
        gridTemplateColumns={outerColTemplate.join(' ')}
        gap={3}
        p={3}
      >
        {!categorized && <GridItem>{data.position}.</GridItem>}
        <GridItem>{data.name}</GridItem>
        {showGroup && data.groupName && (
          <GridItem gridColumn="group">
            {isGroupLink ? (
              <Link to={joinPath(AbsolutePaths.TEAMS, 'details', data.groupId)}>
                <ChakraLink textDecoration="underline">{data.groupName}</ChakraLink>
              </Link>
            ) : (
              data.groupName
            )}
          </GridItem>
        )}
        {(data.score || data.total) && (
          <GridItem gridColumn="score" justifySelf="end">
            {`${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}`}
          </GridItem>
        )}
        {collapsable && <GridItem gridColumn="chevron">{isOpen ? <ChevronUpIcon /> : <ChevronDownIcon />}</GridItem>}
      </Grid>

      {showDescription && data.description && (
        <Box pt="0" pb="4px" bg={bg}>
          {data.description}
        </Box>
      )}

      {isOpen && (
        <Grid gap={3} gridTemplateColumns={innerColTemplate.join(' ')} bg={bg} p={3} pt={0}>
          {data.items
            ?.sort((a, b) => b.value - a.value)
            .map((item, itemIndex) => (
              <>
                {categorized && <GridItem gridColumn="place">{itemIndex + 1}.</GridItem>}
                <GridItem gridColumn="name">{item.name}</GridItem>
                <GridItem justifySelf="end">{`${new Intl.NumberFormat('hu-HU').format(item.value)} ${suffix || ''}`}</GridItem>
              </>
            ))}
        </Grid>
      )}
    </>
  )
}
