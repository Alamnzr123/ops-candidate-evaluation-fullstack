import CrudPage, { type Column } from '../components/CrudPage';
import type { Tier } from '../types';

const columns: Column<Tier>[] = [
  { key: 'id', label: 'ID' },
  { key: 'code', label: 'Code' },
  { key: 'name', label: 'Name' },
];

export default function TierPage() {
  return <CrudPage<Tier & Record<string, unknown>> resource="tier" columns={columns} title="Tiers" idField="id" />;
}