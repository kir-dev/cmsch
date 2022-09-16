import { ChevronDownIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Td, useDisclosure, Tr } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'

type CollapsableTableRowProps = {
  collapsable: boolean
  data: LeaderBoardItemView
  idx: number
  showGroup: boolean
  suffix?: string
}

export const CollapsableTableRow = ({ collapsable, data, idx, suffix, showGroup }: CollapsableTableRowProps) => {
  const { isOpen, onToggle } = useDisclosure()
  return (
    <>
      <Tr onClick={onToggle} _hover={{ cursor: collapsable ? 'pointer' : 'default' }} alignItems="start" fontWeight="bold">
        <>
          <Td p={1} w="1rem">
            {collapsable && (isOpen ? <ChevronDownIcon boxSize={5} /> : <ChevronRightIcon boxSize={5} />)}
          </Td>
          <Td w="1rem">{idx + 1}.</Td>
          <Td>{data.name}</Td>
          {showGroup && <Td>{data.groupName}</Td>}
          <Td w="5rem">{`${data.score || data.total} ${suffix || ''}`}</Td>
        </>
      </Tr>
      {isOpen &&
        data.items?.map((item) => (
          <>
            <Tr></Tr>
            <Tr>
              <Td colSpan={2}></Td>
              <Td>{item.name}</Td>
              <Td>{`${item.value} ${suffix || ''}`}</Td>
            </Tr>
          </>
        ))}
    </>
  )
}
