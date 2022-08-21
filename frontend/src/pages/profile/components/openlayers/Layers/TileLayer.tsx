import { useContext, useEffect } from 'react'
import MapContext from '../MapContext'
import OLTileLayer from 'ol/layer/Tile'
import OLTileSource from 'ol/source/Tile'

type TileLayerProps = {
  source: OLTileSource
  zIndex?: number
}

const TileLayer = ({ source, zIndex = 0 }: TileLayerProps) => {
  const map = useContext(MapContext)
  useEffect(() => {
    if (!map) return

    let tileLayer = new OLTileLayer({
      source,
      zIndex
    })
    map.addLayer(tileLayer)
    tileLayer.setZIndex(zIndex)
    return () => {
      if (map) {
        map.removeLayer(tileLayer)
      }
    }
  }, [map])
  return null
}
export default TileLayer
