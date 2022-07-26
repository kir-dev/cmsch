import { Box, Flex, Icon, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronRight } from 'react-icons/fa'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const ChildNavItem = ({ menu }: Props) => (
  <LinkComponent url={menu.url || '#'} external={menu.external}>
    <Box role="group" display="block" p={2} rounded="md">
      <Stack direction="row" align="center">
        <Box>
          <Text transition="all .3s ease" _groupHover={{ color: useColorModeValue('brand.500', 'brand.400') }} fontWeight={500}>
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
          <Icon color={useColorModeValue('brand.500', 'brand.400')} w={5} h={5} as={FaChevronRight} />
        </Flex>
      </Stack>
    </Box>
  </LinkComponent>
)
