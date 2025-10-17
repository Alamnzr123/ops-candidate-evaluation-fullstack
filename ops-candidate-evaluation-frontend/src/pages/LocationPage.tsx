import CrudPage, {type Column } from '../components/CrudPage';
import type { Location } from '../types';

const columns: Column<Location>[] = [
  { key: 'id', label: 'ID' },
  { key: 'code', label: 'Code' },
  { key: 'name', label: 'Name' },
];

export default function LocationPage() {
  return <CrudPage<Location & Record<string, unknown>> resource="location" columns={columns} title="Locations" idField="id" />;
}