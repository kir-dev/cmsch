import { ChevronDownIcon, ChevronRightIcon } from '@chakra-ui/icons'
import { Td, useColorModeValue, useDisclosure, Box, Tr, Collapse, Table, TableContainer, Tbody, Text } from '@chakra-ui/react'
import { NavItem } from '../util/configs/nav.config'
import { HasChildren } from '../util/react-types.util'
import { LeaderBoardDetail, LeaderBoardItemView } from '../util/views/leaderBoardView'

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
    <Tr onClick={onToggle} _hover={{ cursor: 'pointer' }}>
      <>
        <Td w="1rem">{collapsable && (isOpen ? <ChevronDownIcon boxSize={5} /> : <ChevronRightIcon boxSize={5} />)}</Td>
        <Td>{idx + 1}.</Td>
        <Td>
          {data.name}
          {collapsable &&
            isOpen &&
            data.items?.map((item) => (
              <>
                <br />
                {item.name}
              </>
            ))}
        </Td>
        {showGroup && <Td>{data.groupName}</Td>}
        <Td>
          {`${data.score || data.total} ${suffix || ''}`}
          {collapsable &&
            isOpen &&
            data.items?.map((item) => (
              <>
                <br />
                {item.value}
              </>
            ))}
        </Td>
      </>
    </Tr>
  )
}
