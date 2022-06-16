import { Box, Flex, Icon, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronRight } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { Menu } from '../../api/contexts/config/types'

type Props = {
  menu: Menu
}

export const DesktopSubNav = ({ menu }: Props) => (
  <Link to={menu.url || '#'}>
    <Box role="group" display="block" p={2} rounded="md">
      <Stack direction="row" align="center">
        <Box>
          <Text transition="all .3s ease" _groupHover={{ color: useColorModeValue('brand.500', 'brand.600') }} fontWeight={500}>
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
          <Icon color={useColorModeValue('brand.500', 'brand.600')} w={5} h={5} as={FaChevronRight} />
        </Flex>
      </Stack>
    </Box>
  </Link>
)
