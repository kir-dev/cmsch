import { ChevronDownIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Td, Tr, useDisclosure } from '@chakra-ui/react'
import { useOpaqueBackground } from '../util/core-functions.util'
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
  categorized = false,
  showDescription
}: CollapsableTableRowProps) => {
  const { isOpen, onToggle } = useDisclosure()
  const bg = useOpaqueBackground(1)
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
          {collapsable && <Td w="1rem">{isOpen ? <ChevronDownIcon boxSize={5} /> : <ChevronRightIcon boxSize={5} />}</Td>}
          {!categorized && <Td w="1rem">{data.position}.</Td>}
          {data.score || data.total ? (
            <Td w="5rem">{`${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}`}</Td>
          ) : (
            <Td w="5rem" />
          )}
          <Td colSpan={categorized ? 3 : 2}>{data.name}</Td>
          {showDescription && <Td>{data.description ?? ''}</Td>}
          {showGroup && 'groupName' in data ? <Td>{data.groupName}</Td> : <Td />}
        </>
      </Tr>
      {isOpen &&
        data.items
          ?.sort((a, b) => b.value - a.value)
          .map((item, itemIndex) => (
            <>
              <Tr />
              <Tr>
                <Td w="1rem" />
                {categorized && <Td w="1rem">{itemIndex + 1}.</Td>}
                <Td colSpan={categorized ? 3 : 4}>{item.name}</Td>
                <Td w="5rem">{`${new Intl.NumberFormat('hu-HU').format(item.value)} ${suffix || ''}`}</Td>
              </Tr>
            </>
          ))}
    </>
  )
}
