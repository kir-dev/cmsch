import { ChevronDownIcon, ChevronUpIcon } from '@chakra-ui/icons'
import { Td, Tr, useDisclosure, Link as ChakraLink } from '@chakra-ui/react'
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
  const bg = useOpaqueBackground(1)
  const isGroupLink = typeof data.groupId !== 'undefined'

  let colCount = 3
  if (collapsable) colCount++
  if (showGroup) colCount++

  return (
    <>
      <Tr
        onClick={() => {
          if (collapsable) onToggle()
        }}
        _hover={{ cursor: collapsable ? 'pointer' : 'default' }}
        alignItems="start"
        fontWeight="bold"
        bg={idx % 2 === 0 ? bg : undefined}
      >
        <>
          {!categorized && <Td>{data.position}.</Td>}
          <Td colSpan={categorized ? 2 : 1}>{data.name}</Td>
          {showGroup && data.groupName ? (
            <Td>
              {isGroupLink ? (
                <Link to={joinPath(AbsolutePaths.TEAMS, 'details', data.groupId)}>
                  <ChakraLink textDecoration="underline">{data.groupName}</ChakraLink>
                </Link>
              ) : (
                data.groupName
              )}
            </Td>
          ) : (
            <Td />
          )}
          {data.score || data.total ? (
            <Td>{`${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}`}</Td>
          ) : (
            <Td />
          )}
          {collapsable && <Td textAlign="right">{isOpen ? <ChevronUpIcon boxSize={5} /> : <ChevronDownIcon boxSize={5} />}</Td>}
        </>
      </Tr>
      {showDescription && data.description && (
        <Tr>
          <Td pt="0" pb="4px" colSpan={colCount} bg={idx % 2 === 0 ? bg : undefined}>
            {data.description}
          </Td>
        </Tr>
      )}
      {isOpen &&
        data.items
          ?.sort((a, b) => b.value - a.value)
          .map((item, itemIndex) => (
            <>
              <Tr bg={idx % 2 === 0 ? bg : undefined}>
                {categorized && <Td w="1rem">{itemIndex + 1}.</Td>}
                <Td colSpan={categorized ? 2 : 3}>{item.name}</Td>
                <Td>{`${new Intl.NumberFormat('hu-HU').format(item.value)} ${suffix || ''}`}</Td>
                <Td /> {/* needed so that the background color covers the entire line */}
              </Tr>
            </>
          ))}
    </>
  )
}
