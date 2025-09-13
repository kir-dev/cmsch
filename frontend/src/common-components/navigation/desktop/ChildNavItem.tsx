import { Box, Flex, Icon, Stack, Text } from '@chakra-ui/react'
import { FaChevronRight } from 'react-icons/fa'
import { Menu } from '../../../api/contexts/config/types'
import { useBrandColor } from '../../../util/core-functions.util.ts'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const ChildNavItem = ({ menu }: Props) => {
  const color = useBrandColor(500, 400)
  return (
    <LinkComponent url={menu.url || '#'} external={menu.external}>
      <Box role="group" display="block" p={2} rounded="md">
        <Stack direction="row" align="center">
          <Box>
            <Text transition="all .3s ease" _groupHover={{ color: color }} fontWeight={500}>
              {menu.name}
            </Text>
          </Box>
          <Flex
            transition="all .3s ease"
            transform="translateX(-10px)"
            opacity={0}
            _groupHover={{ opacity: '100%', transform: 'translateX(0)' }}
            justify="flex-end"
            align="center"
            flex={1}
          >
            <Icon color={color} w={5} h={5} as={FaChevronRight} />
          </Flex>
        </Stack>
      </Box>
    </LinkComponent>
  )
}
