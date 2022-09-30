import { ChevronDownIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Td, Tr, useDisclosure } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'

type CollapsableTableRowProps = {
  collapsable: boolean
  data: LeaderBoardItemView
  idx: number
  showGroup: boolean
  suffix?: string
  categorized?: boolean
}

export const CollapsableTableRow = ({ collapsable, data, idx, suffix, showGroup, categorized = false }: CollapsableTableRowProps) => {
  const { isOpen, onToggle } = useDisclosure()
  return (
    <>
      <Tr
        onClick={() => {
          if (collapsable) onToggle()
        }}
        _hover={{ cursor: collapsable ? 'pointer' : 'default' }}
        alignItems="start"
        fontWeight="bold"
      >
        <>
          <Td w="1rem">{collapsable && (isOpen ? <ChevronDownIcon boxSize={5} /> : <ChevronRightIcon boxSize={5} />)}</Td>
          {!categorized && <Td w="1rem">{idx + 1}.</Td>}
          <Td colSpan={categorized ? 3 : 2}>{data.name}</Td>
          {showGroup && 'groupName' in data ? <Td>{data.groupName}</Td> : <Td />}
          {data.score || data.total ? (
            <Td w="5rem">{`${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}`}</Td>
          ) : (
            <Td w="5rem" />
          )}
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
